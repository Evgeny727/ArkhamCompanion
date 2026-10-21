package com.arkhamcompanion.domain.model.settings

import com.arkhamcompanion.domain.objects.ImmutableStringSetSerializer
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    @Serializable(with = ImmutableStringSetSerializer::class)
    val packs: ImmutableSet<String> = persistentSetOf(),
    @Serializable(with = ImmutableStringSetSerializer::class)
    val reprintPacks: ImmutableSet<String> = persistentSetOf(),
)

fun Collection.isEmpty(): Boolean = packs.isEmpty() && reprintPacks.isEmpty()
fun Collection.isNotEmpty(): Boolean = !isEmpty()