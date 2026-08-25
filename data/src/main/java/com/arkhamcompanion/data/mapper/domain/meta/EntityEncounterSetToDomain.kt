package com.arkhamcompanion.data.mapper.domain.meta

import com.arkhamcompanion.data.local.meta.EncounterSetEntity

internal fun EncounterSetEntity.toDomain(): Pair<String, String> = code to name