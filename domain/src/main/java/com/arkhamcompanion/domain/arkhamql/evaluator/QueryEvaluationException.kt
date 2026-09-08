package com.arkhamcompanion.domain.arkhamql.evaluator

import com.arkhamcompanion.domain.arkhamql.QueryError

data class QueryEvaluationException(
    val error: QueryError,
) : RuntimeException()