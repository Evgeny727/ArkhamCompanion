package com.arkhamcompanion.domain.utils

object SpecialCardCodes {
    /** Connected to parallel roland's front. */
    val DIRECTIVES = setOf("90025", "90026", "90027", "90028", "90029")
    /** Parallel front has deckbuilding impact. */
    const val PARALLEL_ROLAND = "90024"
    /** Parallel front has deckbuilding impact. */
    const val PARALLEL_WENDY = "90037"
    /** Random basic weakness placeholder. */
    const val RANDOM_BASIC_WEAKNESS = "01000"
    /** Separate upgrade path. */
    const val TIDAL_MEMENTO = "90038"
    /** adds deckbuilding requirements. */
    val GENERIC_CUSTOM_INVESTIGATORS = setOf("zcu_00001", "zcu_00002", "zcu_00003", "custom_001")
}