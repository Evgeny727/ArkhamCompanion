package com.arkhamcompanion.domain.arkhamql.ast

enum class QueryFieldType {
    BOOLEAN,
    NUMBER,
    STRING,
}

data class QueryField(
    val name: String,
    val aliases: Set<String> = emptySet(),
    val type: QueryFieldType,
)