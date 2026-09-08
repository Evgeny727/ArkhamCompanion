package com.arkhamcompanion.domain.arkhamql.lexer

import com.arkhamcompanion.domain.arkhamql.ast.SymbolType

sealed interface QueryToken {

    val offset: Int

    data class Identifier(
        val value: String,
        override val offset: Int,
    ) : QueryToken

    data class StringLiteral(
        val value: String,
        override val offset: Int,
    ) : QueryToken

    data class RegexLiteral(
        val pattern: String,
        override val offset: Int,
    ) : QueryToken

    data class NumberLiteral(
        val value: Int,
        override val offset: Int,
    ) : QueryToken

    data class BooleanLiteral(
        val value: Boolean,
        override val offset: Int,
    ) : QueryToken

    data class NullLiteral(
        override val offset: Int,
    ) : QueryToken

    data class Operator(
        val type: SymbolType,
        override val offset: Int,
    ) : QueryToken

    data class End(
        override val offset: Int,
    ) : QueryToken
}

enum class GeneralSymbol(override val value: String) : SymbolType {
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),

    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),

    COMMA(","),
}