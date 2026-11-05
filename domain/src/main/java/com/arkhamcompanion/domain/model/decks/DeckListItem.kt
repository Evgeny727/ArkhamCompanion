package com.arkhamcompanion.domain.model.decks

import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList

data class DeckListItem(
    val id: String,
    val name: String,
    val problem: String?,
    val source: DeckSource,
    val cardPool: ResolvedEnvironment,
    val tags: ImmutableList<String>,
    val xpSpent: Int?,
    val xp: Int?,
    val totalXp: Int,
    val investigator: DeckItemInvestigator?,
    val totalCards: Int,
)

data class DeckItemInvestigator(
    val name: String,
    val parallel: Boolean,
    val faction: Faction,
    val faction2: Faction?,
    val faction3: Faction?,
    val thumbnailUrl: String?,
    val imageUrl: String?
)
