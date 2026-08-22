package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.GetTranslationDataQuery
import com.arkhamcompanion.data.local.meta.CycleEntity
import com.arkhamcompanion.data.local.meta.EncounterSetEntity
import com.arkhamcompanion.data.local.meta.FactionEntity
import com.arkhamcompanion.data.local.meta.PackEntity
import com.arkhamcompanion.data.local.meta.TabooSetEntity
import com.arkhamcompanion.fragment.Cycle
import com.arkhamcompanion.fragment.EncounterSet
import com.arkhamcompanion.fragment.Pack
import com.arkhamcompanion.fragment.TabooSet

/**
 * Extension function to convert [Cycle] to [CycleEntity]
 */
fun Cycle.toEntity(name: String): CycleEntity {
    return CycleEntity(
        code = code,
        realName = real_name,
        name = name,
        position = position,
        official = official,
        chapter = chapter ?: (if (official) 2 else null),
    )
}

/**
 * Extension function to convert [Pack] to [PackEntity]
 */
fun Pack.toEntity(name: String): PackEntity {
    return PackEntity(
        code = code,
        cycleCode = cycle_code,
        realName = real_name,
        name = name,
        position = position,
        official = official,
        reprint = reprint ?: false,
        chapter = chapter ?: (if (official) 2 else null),
    )
}

/**
 * Extension function to convert [EncounterSet] to [EncounterSetEntity]
 */
fun EncounterSet.toEntity(translation: EncounterSet?): EncounterSetEntity {
    return EncounterSetEntity(
        code = code,
        name = translation?.name ?: name,
    )
}

/**
 * Extension function to convert [TabooSet] to [TabooSetEntity]
 */
fun TabooSet.toEntity(): TabooSetEntity {
    return TabooSetEntity(
        id = id,
        name = name,
        code = code,
        active = active,
        date = date,
        cardCount = card_count,
        current = current
    )
}

/**
 * Extension function to convert [GetTranslationDataQuery.Faction_name] to [FactionEntity]
 */
fun GetTranslationDataQuery.Faction_name.toEntity(): FactionEntity {
    return FactionEntity(
        code = code,
        name = name
    )
}