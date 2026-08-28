package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.domain.model.cards.CardSearchResult
import kotlinx.collections.immutable.toImmutableList

fun List<CardSearchResultEntity>.toDomain() = map { entity ->
    entity.toDomain()
}.toImmutableList()

fun CardSearchResultEntity.toDomain() = CardSearchResult(
    id = id,
    code = code,
    tabooSetId = tabooSetId
)