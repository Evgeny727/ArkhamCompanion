package com.arkhamcompanion.domain.arkhamql.evaluator

import com.arkhamcompanion.domain.arkhamql.ast.QueryValue

sealed interface EvaluatedValues {
    fun any(function: (QueryValue) -> Boolean): Boolean

    data class Single(
        val value: QueryValue,
    ) : EvaluatedValues {
        override fun any(function: (QueryValue) -> Boolean): Boolean {
            return function(value)
        }
    }

    data class Multiple(
        val values: List<QueryValue>,
    ) : EvaluatedValues {
        override fun any(function: (QueryValue) -> Boolean): Boolean {
            return values.any(function)
        }
    }
}