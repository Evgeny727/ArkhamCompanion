package com.arkhamcompanion.domain.arkhamql

import com.arkhamcompanion.domain.arkhamql.ast.QueryExpression

sealed interface QueryParseResult {
    data class Success(
        val expression: QueryExpression,
    ) : QueryParseResult

    data class Error(
        val error: QueryError,
    ) : QueryParseResult
}
