package com.arkhamcompanion.data.mapper.domain.meta

import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.domain.enums.CardSubType

internal fun CardSubtypeEntity.toDomain(): Pair<CardSubType, String> =
    CardSubType.bySubType(code) to name