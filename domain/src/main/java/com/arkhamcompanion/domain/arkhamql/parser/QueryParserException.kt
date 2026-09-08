package com.arkhamcompanion.domain.arkhamql.parser

import com.arkhamcompanion.domain.arkhamql.QueryError

class QueryParserException(
    val error: QueryError,
) : IllegalArgumentException()