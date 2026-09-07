package com.arkhamcompanion.domain.arkhamql.ast

sealed interface QueryExpression {

    data class Binary(
        val left: QueryExpression,
        val operator: QueryBinaryOperator,
        val right: QueryExpression,
    ) : QueryExpression

    data class Field(
        val reference: QueryFieldReference,
    ) : QueryExpression

    data class Literal(
        val value: QueryValue,
    ) : QueryExpression

    data class List(
        val elements: kotlin.collections.List<QueryExpression>,
    ) : QueryExpression
}