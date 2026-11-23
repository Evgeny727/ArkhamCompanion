package com.arkhamcompanion.domain.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
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

/**
 * Returns this List if it's not `null` and the empty immutable list otherwise.
 */
fun <T> ImmutableList<T>?.orEmpty(): ImmutableList<T> = this ?: persistentListOf()

/**
 * Returns this Map if it's not `null` and the empty immutable map otherwise.
 */
fun <K, V> ImmutableMap<K, V>?.orEmpty(): ImmutableMap<K, V> = this ?: persistentMapOf()