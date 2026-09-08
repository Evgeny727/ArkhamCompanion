package com.arkhamcompanion.domain.arkhamql.lexer

import com.arkhamcompanion.domain.arkhamql.QueryError

class QueryLexerException(
    val error: QueryError
) : IllegalArgumentException()