package com.arkhamcompanion.domain.arkhamql.lexer

import com.arkhamcompanion.domain.arkhamql.QueryError
import com.arkhamcompanion.domain.arkhamql.ast.QueryArithmeticOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryComparisonOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryLogicalOperator

class QueryLexer(
    private val input: String,
) {

    private var offset = 0
    private var previousToken: QueryToken? = null

    fun tokenize(): List<QueryToken> {
        val tokens = buildList {
            while (!isAtEnd()) {
                skipWhitespace()

                if (isAtEnd()) {
                    break
                }

                val token = nextToken()
                add(token)
                previousToken = token
            }

            add(QueryToken.End(offset))
        }

        return tokens
    }

    private fun nextToken(): QueryToken {
        val char = peek()

        return when {
            char == '"' || char == '\'' ->
                readString(char)

            char == '/' && shouldParseRegex() ->
                readRegex()

            char.isDigit() ->
                readNumber()

            char == '-' && peekNext().isDigit() ->
                readNumber()

            isIdentifierStart(char) ->
                readIdentifier()

            else ->
                readSymbol()
        }
    }

    private fun readString(quote: Char): QueryToken.StringLiteral {
        val startOffset = offset

        advance() // opening quote

        val value = buildString {
            while (!isAtEnd() && peek() != quote) {
                if (peek() == '\\') {
                    advance()

                    if (isAtEnd()) {
                        break
                    }

                    when (val escaped = advance()) {
                        'n' -> append('\n')
                        't' -> append('\t')
                        'r' -> append('\r')
                        '\\' -> append('\\')
                        '"' -> append('"')
                        '\'' -> append('\'')

                        // Same behavior as the reference lexer:
                        // unknown escapes lose the backslash.
                        else -> append(escaped)
                    }
                } else {
                    append(advance())
                }
            }
        }

        if (isAtEnd()) {
            throw QueryLexerException(
                QueryError.UnterminatedStringLiteral(startOffset)
            )
        }

        advance() // closing quote

        return QueryToken.StringLiteral(
            value = value,
            offset = startOffset,
        )
    }

    private fun readRegex(): QueryToken.RegexLiteral {
        val startOffset = offset

        advance() // opening /

        val pattern = buildString {
            while (!isAtEnd() && peek() != '/') {
                if (peek() == '\\') {
                    append(advance())

                    if (!isAtEnd()) {
                        append(advance())
                    }
                } else {
                    append(advance())
                }
            }
        }

        if (isAtEnd()) {
            throw QueryLexerException(
                QueryError.UnterminatedRegexLiteral(startOffset)
            )
        }

        advance() // closing /

        return QueryToken.RegexLiteral(
            pattern = pattern,
            offset = startOffset,
        )
    }

    private fun readNumber(): QueryToken.NumberLiteral {
        val startOffset = offset
        var negative = false

        if (peek() == '-') {
            negative = true
            advance()
        }

        val startDigits = offset

        while (!isAtEnd() && peek().isDigit()) {
            advance()
        }

        val value = input
            .substring(startDigits, offset)
            .toInt()

        return QueryToken.NumberLiteral(
            value = if (negative) -value else value,
            offset = startOffset,
        )
    }

    private fun readIdentifier(): QueryToken {
        val startOffset = offset

        while (!isAtEnd() && isIdentifierPart(peek())) {
            advance()
        }

        val value = input.substring(startOffset, offset)

        return when (val normalizedValue = value.lowercase()) {
            "true" -> QueryToken.BooleanLiteral(
                value = true,
                offset = startOffset,
            )

            "false" -> QueryToken.BooleanLiteral(
                value = false,
                offset = startOffset,
            )

            "null" -> QueryToken.NullLiteral(
                offset = startOffset,
            )

            else -> QueryToken.Identifier(
                value = normalizedValue,
                offset = startOffset,
            )
        }
    }

    private fun readSymbol(): QueryToken.Operator {
        val startOffset = offset

        val type = when {
            match("!==") -> QueryComparisonOperator.EXACT_NOT_EQUALS
            match("!??") -> QueryComparisonOperator.EXACT_NOT_CONTAINS

            match("==") -> QueryComparisonOperator.EXACT_EQUALS
            match("!=") -> QueryComparisonOperator.NOT_EQUALS
            match("!?") -> QueryComparisonOperator.NOT_CONTAINS
            match("??") -> QueryComparisonOperator.EXACT_CONTAINS

            match(">=") -> QueryComparisonOperator.GREATER_OR_EQUAL
            match("<=") -> QueryComparisonOperator.LESS_OR_EQUAL

            match("=") -> QueryComparisonOperator.EQUALS
            match("?") -> QueryComparisonOperator.CONTAINS
            match(">") -> QueryComparisonOperator.GREATER_THAN
            match("<") -> QueryComparisonOperator.LESS_THAN

            match("&") -> QueryLogicalOperator.AND
            match("|") -> QueryLogicalOperator.OR

            match("+") -> QueryArithmeticOperator.ADD
            match("-") -> QueryArithmeticOperator.SUBTRACT
            match("*") -> QueryArithmeticOperator.MULTIPLY
            match("/") -> QueryArithmeticOperator.DIVIDE
            match("%") -> QueryArithmeticOperator.MODULO

            match("(") -> GeneralSymbol.LEFT_PAREN
            match(")") -> GeneralSymbol.RIGHT_PAREN

            match("[") -> GeneralSymbol.LEFT_BRACKET
            match("]") -> GeneralSymbol.RIGHT_BRACKET

            match(",") -> GeneralSymbol.COMMA

            else -> {
                throw QueryLexerException(
                    QueryError.UnexpectedCharacter(peek(), startOffset)
                )
            }
        }

        return QueryToken.Operator(
            type = type,
            offset = startOffset,
        )
    }

    /**
     * '/' is a regex literal only when we're in a value position.
     *
     * Regex can appear:
     * - at the beginning of input
     * - after a comparison operator
     * - after '['
     * - after ','
     *
     * This prevents:
     *
     *     cost / 2
     *
     * from being interpreted as a regex.
     */
    private fun shouldParseRegex(): Boolean {
        val previous = previousToken ?: return true

        if (previous !is QueryToken.Operator) {
            return false
        }

        return previous.type in REGEX_CONTEXT_OPERATORS
    }

    private fun isAtEnd(): Boolean =
        offset >= input.length

    private fun peek(): Char =
        if (isAtEnd()) '\u0000' else input[offset]

    private fun peekNext(): Char =
        if (offset + 1 >= input.length) {
            '\u0000'
        } else {
            input[offset + 1]
        }

    private fun advance(): Char =
        input[offset++]

    private fun match(value: String): Boolean {
        if (!input.startsWith(value, offset)) {
            return false
        }

        offset += value.length
        return true
    }

    private fun skipWhitespace() {
        while (!isAtEnd() && peek().isWhitespace()) {
            advance()
        }
    }

    private fun isIdentifierStart(char: Char): Boolean =
        char.isLetter() || char == '_'

    private fun isIdentifierPart(char: Char): Boolean =
        char.isLetterOrDigit() ||
                char == '_' ||
                char == ':'

    private companion object {

        val REGEX_CONTEXT_OPERATORS = setOf(
            QueryComparisonOperator.EQUALS,
            QueryComparisonOperator.EXACT_EQUALS,
            QueryComparisonOperator.NOT_EQUALS,
            QueryComparisonOperator.EXACT_NOT_EQUALS,
            QueryComparisonOperator.CONTAINS,
            QueryComparisonOperator.EXACT_CONTAINS,
            QueryComparisonOperator.NOT_CONTAINS,
            QueryComparisonOperator.EXACT_NOT_CONTAINS,
            GeneralSymbol.LEFT_BRACKET,
            GeneralSymbol.COMMA,
        )
    }
}