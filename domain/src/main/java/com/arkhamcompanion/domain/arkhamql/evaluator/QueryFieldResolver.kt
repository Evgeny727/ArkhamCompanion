package com.arkhamcompanion.domain.arkhamql.evaluator

import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldReference

interface QueryFieldResolver<T> {

    fun resolve(
        reference: QueryFieldReference,
        card: T,
        matchBacks: Boolean = false,
        matchReal: Boolean = false,
    ): EvaluatedValues
}