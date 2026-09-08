package com.arkhamcompanion.domain.arkhamql.ast

enum class QueryBinaryOperator(val value: String) {
    // Logical
    AND("&"),
    OR("|"),

    // Comparison
    EQUALS("="),
    EXACT_EQUALS("=="),
    NOT_EQUALS("!="),
    EXACT_NOT_EQUALS("!=="),

    CONTAINS("?"),
    EXACT_CONTAINS("??"),
    NOT_CONTAINS("!?"),
    EXACT_NOT_CONTAINS("!??"),

    GREATER_THAN(">"),
    GREATER_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_OR_EQUAL("<="),

    // Arithmetic
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%"),
}