package com.arkhamcompanion.domain.arkhamql.parser

import com.arkhamcompanion.domain.arkhamql.ast.QueryArithmeticOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryBinaryOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryComparisonOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryExpression
import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldReference
import com.arkhamcompanion.domain.arkhamql.ast.QueryLogicalOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryValue
import com.arkhamcompanion.domain.arkhamql.ast.SymbolType
import com.arkhamcompanion.domain.arkhamql.fields.QueryFieldRegistry
import com.arkhamcompanion.domain.arkhamql.lexer.GeneralSymbol
import com.arkhamcompanion.domain.arkhamql.lexer.QueryToken

class QueryParser(
    private val tokens: List<QueryToken>,
    private val fieldRegistry: QueryFieldRegistry,
) {

    private var position = 0

    fun parse(): QueryExpression {
        val expression = parseOr()

        expectEnd()

        return expression
    }

    /**
     * OR has the lowest precedence.
     *
     * a | b & c
     *
     * becomes
     *
     * a | (b & c)
     */
    private fun parseOr(): QueryExpression {
        var expression = parseAnd()

        while (matchOperator(QueryLogicalOperator.OR)) {
            val right = parseAnd()

            expression = QueryExpression.Binary(
                left = expression,
                operator = QueryBinaryOperator.OR,
                right = right,
            )
        }

        return expression
    }

    /**
     * AND has higher precedence than OR.
     */
    private fun parseAnd(): QueryExpression {
        var expression = parseComparison()

        while (matchOperator(QueryLogicalOperator.AND)) {
            val right = parseComparison()

            expression = QueryExpression.Binary(
                left = expression,
                operator = QueryBinaryOperator.AND,
                right = right,
            )
        }

        return expression
    }

    private fun parseComparison(): QueryExpression {
        val left = parseAddition()

        val operator = matchComparisonOperator()
            ?: return left

        val right = parseAddition()

        if (isComparisonOperator(peek())) {
            throw error(
                "Comparison operators cannot be chained"
            )
        }

        return QueryExpression.Binary(
            left = left,
            operator = operator,
            right = right,
        )
    }

    /**
     * '+' and '-'.
     */
    private fun parseAddition(): QueryExpression {
        var expression = parseMultiplication()

        while (true) {
            val operator = when {
                matchOperator(QueryArithmeticOperator.ADD) ->
                    QueryBinaryOperator.ADD

                matchOperator(QueryArithmeticOperator.SUBTRACT) ->
                    QueryBinaryOperator.SUBTRACT

                else -> break
            }

            val right = parseMultiplication()

            expression = QueryExpression.Binary(
                left = expression,
                operator = operator,
                right = right,
            )
        }

        return expression
    }

    /**
     * *, / and %.
     */
    private fun parseMultiplication(): QueryExpression {
        var expression = parsePrimary()

        while (true) {
            val operator = when {
                matchOperator(QueryArithmeticOperator.MULTIPLY) ->
                    QueryBinaryOperator.MULTIPLY

                matchOperator(QueryArithmeticOperator.DIVIDE) ->
                    QueryBinaryOperator.DIVIDE

                matchOperator(QueryArithmeticOperator.MODULO) ->
                    QueryBinaryOperator.MODULO

                else -> break
            }

            val right = parsePrimary()

            expression = QueryExpression.Binary(
                left = expression,
                operator = operator,
                right = right,
            )
        }

        return expression
    }

    private fun parsePrimary(): QueryExpression {
        if (matchOperator(GeneralSymbol.LEFT_PAREN)) {
            val expression = parseOr()

            expectOperator(GeneralSymbol.RIGHT_PAREN)

            return expression
        }

        if (matchOperator(GeneralSymbol.LEFT_BRACKET)) {
            return parseList()
        }

        return when (val token = advance()) {
            is QueryToken.Identifier ->
                parseFieldReference(token)

            is QueryToken.StringLiteral ->
                QueryExpression.Literal(
                    QueryValue.String(token.value)
                )

            is QueryToken.RegexLiteral ->
                QueryExpression.Literal(
                    QueryValue.Regex(token.pattern)
                )

            is QueryToken.NumberLiteral ->
                QueryExpression.Literal(
                    QueryValue.Number(token.value)
                )

            is QueryToken.BooleanLiteral ->
                QueryExpression.Literal(
                    QueryValue.Boolean(token.value)
                )

            is QueryToken.NullLiteral ->
                QueryExpression.Literal(
                    QueryValue.Null
                )

            else -> {
                throw error(
                    "Expected value or field, got ${describe(token)}"
                )
            }
        }
    }

    private fun parseList(): QueryExpression {
        val values = buildList {
            if (matchOperator(GeneralSymbol.RIGHT_BRACKET)) {
                return@buildList
            }

            while (true) {
                add(parseOr())

                if (matchOperator(GeneralSymbol.RIGHT_BRACKET)) {
                    break
                }

                expectOperator(GeneralSymbol.COMMA)
            }
        }

        return QueryExpression.List(values)
    }

    private fun parseFieldReference(
        token: QueryToken.Identifier,
    ): QueryExpression.Field {
        val parts = token.value.split(':')

        if (parts.isEmpty()) {
            throw error("Empty field reference")
        }

        var real = false
        var back = false

        val fieldName = buildList {
            for (part in parts) {
                when (part) {
                    "real" -> {
                        if (real) {
                            throw error(
                                "Duplicate 'real' qualifier"
                            )
                        }

                        real = true
                    }

                    "back" -> {
                        if (back) {
                            throw error(
                                "Duplicate 'back' qualifier"
                            )
                        }

                        back = true
                    }

                    else -> add(part)
                }
            }
        }.joinToString(":")

        if (fieldName.isEmpty()) {
            throw error("Field name is missing")
        }

        val field = fieldRegistry.resolve(fieldName)
            ?: throw error("Unknown field '$fieldName'")

        return QueryExpression.Field(
            QueryFieldReference(
                field = field,
                real = real,
                back = back,
            )
        )
    }

    private fun matchComparisonOperator(): QueryBinaryOperator? {
        val type = (peek() as? QueryToken.Operator)?.type
            ?: return null

        val operator = when (type) {
            QueryComparisonOperator.EQUALS ->
                QueryBinaryOperator.EQUALS

            QueryComparisonOperator.EXACT_EQUALS ->
                QueryBinaryOperator.EXACT_EQUALS

            QueryComparisonOperator.NOT_EQUALS ->
                QueryBinaryOperator.NOT_EQUALS

            QueryComparisonOperator.EXACT_NOT_EQUALS ->
                QueryBinaryOperator.EXACT_NOT_EQUALS

            QueryComparisonOperator.CONTAINS ->
                QueryBinaryOperator.CONTAINS

            QueryComparisonOperator.EXACT_CONTAINS ->
                QueryBinaryOperator.EXACT_CONTAINS

            QueryComparisonOperator.NOT_CONTAINS ->
                QueryBinaryOperator.NOT_CONTAINS

            QueryComparisonOperator.EXACT_NOT_CONTAINS ->
                QueryBinaryOperator.EXACT_NOT_CONTAINS

            QueryComparisonOperator.GREATER_THAN ->
                QueryBinaryOperator.GREATER_THAN

            QueryComparisonOperator.GREATER_OR_EQUAL ->
                QueryBinaryOperator.GREATER_OR_EQUAL

            QueryComparisonOperator.LESS_THAN ->
                QueryBinaryOperator.LESS_THAN

            QueryComparisonOperator.LESS_OR_EQUAL ->
                QueryBinaryOperator.LESS_OR_EQUAL

            else -> return null
        }

        position++

        return operator
    }

    private fun matchOperator(
        type: SymbolType,
    ): Boolean {
        val token = peek()

        if (token !is QueryToken.Operator || token.type != type) {
            return false
        }

        position++

        return true
    }

    private fun expectOperator(
        type: SymbolType,
    ) {
        if (!matchOperator(type)) {
            throw error(
                "Expected '$type', got ${describe(peek())}"
            )
        }
    }

    private fun expectEnd() {
        if (peek() !is QueryToken.End) {
            throw error(
                "Unexpected token ${describe(peek())}"
            )
        }
    }

    private fun peek(): QueryToken =
        tokens.getOrNull(position)
            ?: QueryToken.End(
                offset = tokens.lastOrNull()?.offset ?: 0
            )

    private fun advance(): QueryToken =
        tokens.getOrNull(position++)
            ?: QueryToken.End(
                offset = tokens.lastOrNull()?.offset ?: 0
            )

    private fun isComparisonOperator(
        token: QueryToken,
    ): Boolean =
        token is QueryToken.Operator &&
                token.type in COMPARISON_OPERATORS

    private fun describe(token: QueryToken): String =
        when (token) {
            is QueryToken.Identifier ->
                "identifier '${token.value}'"

            is QueryToken.StringLiteral ->
                "string"

            is QueryToken.RegexLiteral ->
                "regex"

            is QueryToken.NumberLiteral ->
                "number"

            is QueryToken.BooleanLiteral ->
                "boolean"

            is QueryToken.NullLiteral ->
                "null"

            is QueryToken.Operator ->
                "operator '${token.type}'"

            is QueryToken.End ->
                "end of query"
        }

    private fun error(message: String): QueryParserException =
        QueryParserException(message = message + " at position ${peek().offset}")

    private companion object {

        val COMPARISON_OPERATORS = setOf(
            QueryComparisonOperator.EQUALS,
            QueryComparisonOperator.EXACT_EQUALS,
            QueryComparisonOperator.NOT_EQUALS,
            QueryComparisonOperator.EXACT_NOT_EQUALS,
            QueryComparisonOperator.CONTAINS,
            QueryComparisonOperator.EXACT_CONTAINS,
            QueryComparisonOperator.NOT_CONTAINS,
            QueryComparisonOperator.EXACT_NOT_CONTAINS,
            QueryComparisonOperator.GREATER_THAN,
            QueryComparisonOperator.GREATER_OR_EQUAL,
            QueryComparisonOperator.LESS_THAN,
            QueryComparisonOperator.LESS_OR_EQUAL,
        )
    }
}