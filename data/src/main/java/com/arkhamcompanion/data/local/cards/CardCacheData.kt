package com.arkhamcompanion.data.local.cards

import kotlinx.serialization.Serializable

@Serializable
data class CardCacheData(
    val traits: Map<String, Set<String>> = emptyMap(),
    val actions: Map<String, Set<String>> = emptyMap(),
    val properties: Map<String, Set<String>> = emptyMap(),
    val skillBoosts: Map<String, Set<String>> = emptyMap(),
    val uses: Map<String, Set<String>> = emptyMap(),
    val slots: Map<String, Set<String>> = emptyMap(),
    val tags: Map<String, Set<String>> = emptyMap(),
    val requiredCards: Map<String, Set<String>> = emptyMap(),
    val sideDeckRequiredCards: Map<String, Set<String>> = emptyMap(),
    val restrictedTo: Map<String, Set<String>> = emptyMap(),
    val advanced: Map<String, Set<String>> = emptyMap(),
    val replacement: Map<String, Set<String>> = emptyMap(),
    val parallelCards: Map<String, Set<String>> = emptyMap(),
    val parallel: Map<String, Set<String>> = emptyMap(),
    val base: Map<String, Set<String>> = emptyMap(),
    val duplicates: Map<String, Set<String>> = emptyMap(),
    val reprints: Map<String, Set<String>> = emptyMap(),
    val level: Map<String, Set<String>> = emptyMap(),
    val bound: Map<String, Set<String>> = emptyMap(),
    val bonded: Map<String, Set<String>> = emptyMap(),
    val fronts: Map<String, String> = emptyMap(),
    val backs: Map<String, String> = emptyMap(),
    val otherVersions: Map<String, Set<String>> = emptyMap(),
    val basePrints: Map<String, Set<String>> = emptyMap(),
)
