package com.arkhamcompanion.data.repository

import com.arkhamcompanion.data.local.dao.MetaDao
import com.arkhamcompanion.data.mapper.domain.meta.toDomain
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.meta.TabooSet
import com.arkhamcompanion.domain.repository.MetaRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MetaRepositoryImpl @Inject constructor(
    private val metaDao: MetaDao
): MetaRepository {

    override fun getTaboos(): Flow<ImmutableList<TabooSet>> = metaDao.getTaboos().map { sets ->
        sets.map { it.toDomain() }.toImmutableList()
    }

    override fun getAllPacks(secondCore: Boolean): Flow<ImmutableList<Pack>> = metaDao.getAllPacks().map { packs ->
        buildList {
            packs.forEach { entity ->
                val pack = entity.toDomain()
                add(pack)

                if (secondCore && pack.code == "core") {
                    add(
                        pack.copy(
                            code = "core2"
                            // change any other fields if needed
                        )
                    )
                }
            }
        }.toImmutableList()
    }

    override fun getAllFactions(): Flow<ImmutableMap<Faction, String>> = metaDao.getAllFactions()
        .map { factions ->
            factions.map { it.toDomain() }
                .sortedBy { Faction.valueOf(it.first.name).ordinal }
                .toMap()
                .toImmutableMap()
        }

}