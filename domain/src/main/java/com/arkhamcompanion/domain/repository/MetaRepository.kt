package com.arkhamcompanion.domain.repository

import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.meta.TabooSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

interface MetaRepository {

    fun getTaboos(): Flow<ImmutableList<TabooSet>>

    fun getAllPacks(secondCore: Boolean = false): Flow<ImmutableList<Pack>>

    fun getAllFactions(): Flow<ImmutableMap<Faction, String>>

}