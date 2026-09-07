package com.arkhamcompanion.domain.arkhamql.evaluator

data class QueryEvaluationException(
    override val message: String,
) : RuntimeException(message)