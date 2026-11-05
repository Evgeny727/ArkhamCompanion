package com.arkhamcompanion.data.mapper.domain.decks

import com.arkhamcompanion.data.local.decks.DeckListItemEntity
import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.decks.DeckItemInvestigator
import com.arkhamcompanion.domain.model.decks.DeckListItem
import com.arkhamcompanion.domain.objects.splitToWords
import com.arkhamcompanion.domain.utils.EnvironmentClassifier
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun DeckListItemEntity.toDomain(
    environmentClassifier: EnvironmentClassifier
): DeckListItem = DeckListItem(
    id = id,
    name = name,
    problem = problem,
    source = DeckSource.valueOf(source),
    cardPool = environmentClassifier.resolve(cardPool?.split(",").orEmpty()),
    tags = tags.splitToWords().toImmutableList(),
    xpSpent = xpSpent,
    xp = xp?.plus(xpAdjustment ?: 0),
    totalXp = totalXp,
    investigator = investigatorName?.let { name ->
        DeckItemInvestigator(
            name = name,
            parallel = investigatorParallel!!,
            faction = Faction.byFaction(investigatorFaction!!),
            faction2 = investigatorFaction2?.let { Faction.byFaction(it) },
            faction3 = investigatorFaction3?.let { Faction.byFaction(it) },
            thumbnailUrl = investigatorThumbnailUrl,
            imageUrl = investigatorImageUrl
        )
    },
    totalCards = extraDeck.orEmpty().split(",").count(String::isNotBlank) +
            slots.jsonObject.values.sumOf { it.jsonPrimitive.intOrNull ?: 0 }
)