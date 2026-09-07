package com.arkhamcompanion.domain.arkhamql.ast

data class QueryFieldReference(
    val field: QueryField,
    val real: Boolean = false,
    val back: Boolean = false,
)