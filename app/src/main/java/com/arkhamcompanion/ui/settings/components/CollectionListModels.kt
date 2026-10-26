package com.arkhamcompanion.ui.settings.components

import com.arkhamcompanion.domain.model.meta.Pack
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

data class ChapterGroup(
    val chapter: Int?,
    val cycles: ImmutableList<CycleGroup>,
    val reprintPackCodes: ImmutableSet<String>,
    val packCodes: ImmutableSet<String>,
    val removablePackCodes: ImmutableSet<String>,
)

data class CycleGroup(
    val cycleName: String,
    val reprintPacks: ImmutableList<Pack>,
    val reprintPackCodes: ImmutableSet<String>,
    val packs: ImmutableList<Pack>,
    val packCodes: ImmutableSet<String>,
)

internal class ChapterBuilder(val chapter: Int?) {
    val cycles = mutableListOf<CycleBuilder>()
    val reprintPackCodes = mutableListOf<String>()
    val packCodes = mutableListOf<String>()
    val removablePackCodes = mutableListOf<String>()

    fun build() = ChapterGroup(
        chapter = chapter,
        cycles = cycles.map { it.build() }.toImmutableList(),
        reprintPackCodes = reprintPackCodes.toImmutableSet(),
        packCodes = packCodes.toImmutableSet(),
        removablePackCodes = removablePackCodes.toImmutableSet()

    )
}

internal class CycleBuilder(val cycleName: String) {
    val reprintPacks = mutableListOf<Pack>()
    val reprintPackCodes = mutableListOf<String>()
    val packs = mutableListOf<Pack>()
    val packCodes = mutableListOf<String>()

    fun build() = CycleGroup(
        cycleName = cycleName,
        reprintPacks = reprintPacks.toImmutableList(),
        reprintPackCodes = reprintPackCodes.toImmutableSet(),
        packs = packs.toImmutableList(),
        packCodes = packCodes.toImmutableSet()
    )
}

internal val coreCodes = setOf("core", "core2")