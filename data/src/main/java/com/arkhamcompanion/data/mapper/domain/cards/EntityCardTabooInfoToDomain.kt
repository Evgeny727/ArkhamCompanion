package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.data.local.cards.CardTabooInfoEntity
import com.arkhamcompanion.domain.model.cards.CardTabooInfo
import com.arkhamcompanion.domain.objects.CardTextParser

internal fun CardTabooInfoEntity.toDomain() = CardTabooInfo(
    tabooXp = tabooXp,
    tabooTextChange = tabooTextChange?.let { CardTextParser.parse(it) },
    tabooName = tabooName,
    tabooDate = tabooDate,
    tabooPlaceholder = tabooPlaceholder
)