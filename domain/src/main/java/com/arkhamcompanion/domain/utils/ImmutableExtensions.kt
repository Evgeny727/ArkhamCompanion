package com.arkhamcompanion.domain.utils

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlin.collections.plus

/**
 * Removes value if it is in the set, otherwise adds it.
 */
fun <T> ImmutableSet<T>.toggle(value: T): ImmutableSet<T> =
    (if (value in this) minus(value) else plus(value))
        .toImmutableSet()