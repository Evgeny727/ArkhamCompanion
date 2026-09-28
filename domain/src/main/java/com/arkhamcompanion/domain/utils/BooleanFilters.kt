package com.arkhamcompanion.domain.utils

typealias Filter<T> = (T) -> Boolean

fun <T> and(vararg filters: Filter<T>): Filter<T> = { element ->
    filters.all { it(element) }
}

fun <T> or(vararg filters: Filter<T>): Filter<T> = { element ->
    filters.isEmpty() || filters.any { it(element) }
}

fun <T> not(filter: Filter<T>): Filter<T> = { element ->
    !filter(element)
}

fun <T> notUnless(notFilter: Filter<T>, unlessFilters: List<Filter<T>>): Filter<T> = { element ->
    or(*unlessFilters.toTypedArray())(element) || not(notFilter)(element)
}