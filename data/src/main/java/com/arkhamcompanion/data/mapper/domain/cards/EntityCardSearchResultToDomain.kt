package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.domain.model.cards.CardSearchResultItem
import kotlinx.collections.immutable.toImmutableList

fun List<CardSearchResultEntity>.toDomain() = map { entity ->
    entity.toDomain()
}.toImmutableList()

fun CardSearchResultEntity.toDomain() = CardSearchResultItem(
    id = front.id,
    code = front.code,
    tabooSetId = front.tabooSetId
)