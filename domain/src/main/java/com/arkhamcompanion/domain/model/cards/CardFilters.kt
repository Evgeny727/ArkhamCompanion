package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.settings.Collection
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

data class CardFilters(
    val factions: ImmutableSet<Faction> = persistentSetOf(),
    val levelFilter: LevelFilter = LevelFilter(),
    val types: ImmutableSet<CardType> = persistentSetOf(),
    val subTypes: ImmutableSet<CardSubType?> = persistentSetOf(),
    val costFilter: CostFilter = CostFilter(),
    val skillsFilter: SkillsFilter = SkillsFilter(),
    val actions: ImmutableSet<String> = persistentSetOf(),
    val traits: ImmutableSet<String> = persistentSetOf(),
    val healthSanityFilter: HealthSanityFilter = HealthSanityFilter(),
    val assetFilter: AssetFilter = AssetFilter(),
    val propertiesFilter: PropertiesFilter = PropertiesFilter(),
    val enemyFilter: EnemyFilter = EnemyFilter(),
    val locationFilter: LocationFilter = LocationFilter(),
    val encounterSets: ImmutableSet<String> = persistentSetOf(),
    val officialFilter: Boolean? = null,
    val packs: Collection = Collection(persistentSetOf(), persistentSetOf()),
    val tabooSetId: Int? = null,
    val illustrators: ImmutableSet<String> = persistentSetOf(),
)

data class LevelFilter(
    val range: NullableIntRange = NullableIntRange(null, 5),
    val forcedRange: NullableIntRange? = null,
)

data class CostFilter(
    val range: NullableIntRange = NullableIntRange(null, 20),
    val xCost: Boolean = false,
    val evenCost: Boolean = false,
    val oddCost: Boolean = false,
)

data class SkillsFilter(
    val willpower: Int? = null,
    val intellect: Int? = null,
    val combat: Int? = null,
    val agility: Int? = null,
    val wild: Int? = null,
    val any: Int? = null,
)

data class AssetFilter(
    val slots: ImmutableSet<String> = persistentSetOf(),
    val uses: ImmutableSet<String> = persistentSetOf(),
    val skillBoosts: ImmutableSet<String> = persistentSetOf(),
)

data class HealthSanityFilter(
    val health: NullableIntRange = NullableIntRange(null, 15),
    val sanity: NullableIntRange = NullableIntRange(null, 9),
    val includeXHealthOrSanity: Boolean = false,
    val healthPerInvestigator: Boolean = false,
)

data class PropertiesFilter(
    val customizable: Boolean = false,
    val exile: Boolean = false,
    val exceptional: Boolean = false,
    val fast: Boolean = false,
    val healsDamage: Boolean = false,
    val healsHorror: Boolean = false,
    val multiclass: Boolean = false,
    val myriad: Boolean = false,
    val permanent: Boolean = false,
    val seal: Boolean = false,
    val specialist: Boolean = false,
    val succeedBy: Boolean = false,
    val unique: Boolean = false,
    val victory: Boolean = false,
)

data class EnemyFilter(
    val fight: NullableIntRange = NullableIntRange(null, 8),
    val evade: NullableIntRange = NullableIntRange(null, 8),
    val damage: NullableIntRange = NullableIntRange(null, 3),
    val horror: NullableIntRange = NullableIntRange(null, 5),
    val vengeance: Boolean = false,
)

data class LocationFilter(
    val shroud: NullableIntRange = NullableIntRange(null, 9),
    val clues: NullableIntRange = NullableIntRange(null, 12),
    val xShroud: Boolean = false,
    val perInvestigatorClues: Boolean = false,
)

data class NullableIntRange(
    val min: Int?,
    val max: Int?,
) {
    operator fun contains(value: Int?): Boolean {
        if (value == null) return min == null || max == null
        else if (min == null && max == null) return false
        return (min == null || value >= min) && (max == null || value <= max)
    }
}
