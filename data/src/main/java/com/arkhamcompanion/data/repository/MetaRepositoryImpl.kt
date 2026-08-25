package com.arkhamcompanion.data.repository

import com.arkhamcompanion.data.local.dao.MetaDao
import com.arkhamcompanion.data.mapper.domain.meta.toDomain
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.meta.Pack
import com.arkhamcompanion.domain.model.meta.TabooSet
import com.arkhamcompanion.domain.repository.MetaRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
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
                    add(pack.copy(code = "core2"))
                }
            }
        }.toImmutableList()
    }

    override fun getAllFactions(): Flow<ImmutableMap<Faction, String>> = metaDao.getAllFactions()
        .map { factions ->
            factions.map { it.toDomain() }
                .sortedBy { it.first.ordinal }
                .toMap()
                .toImmutableMap()
        }

    override fun getAllTypes(): Flow<ImmutableMap<CardType, String>> = metaDao.getAllTypes()
        .map { types ->
            types.map { it.toDomain() }
                .sortedBy { it.second }
                .toMap()
                .toImmutableMap()
        }

    override fun getAllSubTypes(): Flow<ImmutableMap<CardSubType, String>> = metaDao.getAllSubTypes()
        .map { subtypes ->
            subtypes.map { it.toDomain() }
                .sortedBy { it.first.ordinal }
                .toMap()
                .toImmutableMap()
        }

    override fun getAllActions(): Flow<Array<String>> = CardCache.actionsFlow

    override fun getAllTraits(): Flow<Array<String>> = CardCache.traitsFlow

    override fun getAllSlots(): Flow<Array<String>> = CardCache.slotsFlow

    override fun getAllUses(): Flow<Array<String>> = CardCache.usesFlow

    override fun getAllSkillBoosts(): Flow<Array<String>> = CardCache.skillBoostsFlow

    override fun getAllEncounterSets(): Flow<ImmutableMap<String, String>> = metaDao.getAllEncounterSets()
        .map { encounterSets ->
            encounterSets.map { it.toDomain() }
                .sortedBy { it.second }
                .toMap()
                .toImmutableMap()
        }

    override fun getAllIllustrators(): Flow<ImmutableSet<String>> = metaDao.getAllIllustrators()
        .map { illustrators ->
            illustrators.toImmutableSet()
        }

}