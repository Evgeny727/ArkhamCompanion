package com.arkhamcompanion.domain.arkhamql.parser

class QueryParserException(
    override val message: String,
) : IllegalArgumentException(message)