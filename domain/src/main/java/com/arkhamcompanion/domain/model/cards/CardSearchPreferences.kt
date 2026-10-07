package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.model.settings.Collection
import com.arkhamcompanion.domain.repository.DEFAULT_MYTHOS_SORT_ORDER
import com.arkhamcompanion.domain.repository.DEFAULT_PLAYER_SORT_ORDER
import kotlinx.collections.immutable.persistentSetOf

data class CardSearchPreferences(
    val includeEnglish: Boolean = false,
    val showFanMade: Boolean = false,
    val showPreview: Boolean = false,
    val tabooSetId: Int = 0,
    val playerSortOrder: List<String> = DEFAULT_PLAYER_SORT_ORDER,
    val mythosSortOrder: List<String> = DEFAULT_MYTHOS_SORT_ORDER,
    val ignoreCollection: Boolean = true,
    val collection: Collection = Collection(persistentSetOf(), persistentSetOf())
)
