package com.arkhamcompanion.domain.repository

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.meta.TabooSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow

interface MetaRepository {

    fun getTaboos(): Flow<ImmutableList<TabooSet>>

    fun getAllPacks(secondCore: Boolean = false): Flow<ImmutableList<Pack>>

    fun getAllFactions(): Flow<ImmutableMap<Faction, String>>

    fun getAllTypes(): Flow<ImmutableMap<CardType, String>>

    fun getAllSubTypes(): Flow<ImmutableMap<CardSubType, String>>

    fun getAllActions(): Flow<Array<String>>

    fun getAllTraits(): Flow<Array<String>>

    fun getAllSlots(): Flow<Array<String>>

    fun getAllUses(): Flow<Array<String>>

    fun getAllSkillBoosts(): Flow<Array<String>>

    fun getAllEncounterSets(): Flow<ImmutableMap<String, String>>

    fun getAllIllustrators(): Flow<ImmutableSet<String>>
}