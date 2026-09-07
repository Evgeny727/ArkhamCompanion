package com.arkhamcompanion.domain.arkhamql.fields

import com.arkhamcompanion.domain.arkhamql.ast.QueryField

class QueryFieldRegistry(fields: List<QueryField>) {

    private val fieldsByName = buildMap {
        fields.forEach { field ->
            put(field.name, field)

            field.aliases.forEach { alias ->
                put(alias, field)
            }
        }
    }

    fun resolve(name: String): QueryField? =
        fieldsByName[name.lowercase()]

}