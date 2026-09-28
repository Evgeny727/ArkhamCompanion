package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.Faction
import kotlinx.collections.immutable.ImmutableList

data class Restrictions(
    val faction: ImmutableList<Faction>,
    val investigator: ImmutableList<String>,
    val trait: ImmutableList<String>,
)
