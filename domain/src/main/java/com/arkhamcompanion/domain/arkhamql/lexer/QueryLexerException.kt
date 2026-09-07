package com.arkhamcompanion.domain.arkhamql.lexer

class QueryLexerException(
    override val message: String
) : IllegalArgumentException(message)