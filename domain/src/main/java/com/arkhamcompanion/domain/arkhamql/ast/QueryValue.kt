package com.arkhamcompanion.domain.arkhamql.ast

sealed interface QueryValue {

    data class Boolean(
        val value: kotlin.Boolean
    ) : QueryValue

    data class Number(
        val value: Int
    ) : QueryValue

    data class String(
        val value: kotlin.String
    ) : QueryValue

    data class Regex(
        val pattern: kotlin.String
    ) : QueryValue

    data object Null : QueryValue
}