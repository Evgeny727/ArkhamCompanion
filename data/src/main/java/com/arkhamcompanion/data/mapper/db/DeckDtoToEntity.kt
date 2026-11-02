package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.data.local.decks.DeckEntity
import com.arkhamcompanion.data.remote.dto.DeckDto
import com.arkhamcompanion.domain.objects.TimestampNormalizer.normalizeTimestamp
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun List<DeckDto>.toEntity(): List<DeckEntity> =
    map { it.toEntity() }

private fun DeckDto.toEntity(): DeckEntity =
    DeckEntity(
        dateCreation = normalizeTimestamp(dateCreation),
        dateUpdate = normalizeTimestamp(dateUpdate),
        descriptionMd = descriptionMd,
        exileString = exileString,
        ignoreDeckLimitSlots = ignoreDeckLimitSlots,
        id = id,
        investigatorCode = investigatorCode,
        alternateFrontCode = meta.jsonObject["alternate_front"]?.jsonPrimitive?.contentOrNull,
        alternateBackCode = meta.jsonObject["alternate_back"]?.jsonPrimitive?.contentOrNull,
        meta = meta,
        name = name,
        nextDeck = nextDeck,
        previousDeck = previousDeck,
        problem = problem,
        sideSlots = sideSlots,
        slots = slots,
        extraDeck = meta.jsonObject["extra_deck"]?.jsonPrimitive?.contentOrNull,
        cardPool = meta.jsonObject["card_pool"]?.jsonPrimitive?.contentOrNull,
        source = source,
        tabooId = tabooId,
        tags = tags,
        userId = userId,
        version = version,
        xpAdjustment = xpAdjustment,
        xpSpent = xpSpent,
        xp = xp
    )