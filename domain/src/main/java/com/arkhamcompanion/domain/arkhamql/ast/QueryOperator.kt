package com.arkhamcompanion.domain.arkhamql.ast

interface SymbolType {
    val value: String
}

enum class QueryComparisonOperator(override val value: String) : SymbolType {
    EQUALS("="),
    NOT_EQUALS("!="),
    EXACT_EQUALS("=="),
    EXACT_NOT_EQUALS("!=="),
    CONTAINS("?"),
    NOT_CONTAINS("!?"),
    EXACT_CONTAINS("??"),
    EXACT_NOT_CONTAINS("!??"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<="),
}

enum class QueryArithmeticOperator(override val value: String) : SymbolType {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
}

enum class QueryLogicalOperator(override val value: String) : SymbolType {
    AND("&"),
    OR("|"),
}