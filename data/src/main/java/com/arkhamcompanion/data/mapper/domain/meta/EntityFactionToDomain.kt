package com.arkhamcompanion.data.mapper.domain.meta

import com.arkhamcompanion.data.local.meta.FactionEntity
import com.arkhamcompanion.domain.enums.Faction

internal fun FactionEntity.toDomain(): Pair<Faction, String> =
    Faction.byFaction(code) to name