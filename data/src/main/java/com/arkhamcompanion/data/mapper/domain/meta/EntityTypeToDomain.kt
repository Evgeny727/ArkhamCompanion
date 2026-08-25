package com.arkhamcompanion.data.mapper.domain.meta

import com.arkhamcompanion.data.local.cards.CardTypeEntity
import com.arkhamcompanion.domain.enums.CardType

internal fun CardTypeEntity.toDomain(): Pair<CardType, String> =
    CardType.byType(code) to name