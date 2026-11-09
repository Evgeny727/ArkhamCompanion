package com.arkhamcompanion.domain.utils

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet

/**
 * Removes value if it is in the set, otherwise adds it.
 */
fun <T> ImmutableSet<T>.toggle(value: T): ImmutableSet<T> =
    (if (value in this) minus(value) else plus(value))
        .toImmutableSet()

/**
 * Returns this Set if it's not `null` and the empty immutable set otherwise.
 */
fun <T> ImmutableSet<T>?.orEmpty(): ImmutableSet<T> = this ?: persistentSetOf()