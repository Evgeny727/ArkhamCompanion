package com.arkhamcompanion.data.utils

import androidx.room3.RoomRawQuery
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.data.objects.CardSearchQueryBuilder.buildSortClause
import com.arkhamcompanion.domain.model.cards.CardFilters
import com.arkhamcompanion.domain.model.cards.CardSearchConfig
import com.arkhamcompanion.domain.model.cards.Ownership
import com.arkhamcompanion.domain.model.settings.isEmpty
import com.arkhamcompanion.domain.model.settings.isNotEmpty

internal fun buildCardsListItemsQuery(requested: String) =
    """
        WITH requested(id, position) AS (
            $requested
        )
        SELECT 
            c.id,
            c.code,
            c.duplicate_of_code,
            c.thumbnailurl,
        
            c.cost,
            c.xp,
            c.permanent,
        
            c.taboo_xp,
            c.taboo_set_id,
            c.taboo_placeholder,
        
            c.type_code,
            t.name AS typeName,
        
            c.subtype_code,
            st.name AS subTypeName,
        
            c.faction_code,
            f.name AS factionName,
            c.faction2_code,
            c.faction3_code,
        
            c.pack_code,
            p.name AS packName,
            c.pack_position,
        
            c.encounter_code,
            e.name AS encounterName,
            c.encounter_position,
        
            c.cycle_code,
            cy.name AS cycleName,
            cy.position as cyclePosition,
            
            c.reprint_pack_code,
        
            c.name,
            c.subname,
        
            c.skill_willpower,
            c.skill_intellect,
            c.skill_combat,
            c.skill_agility,
            c.skill_wild,
        
            c.parallel,
            c.is_unique,
            c.slot,
            c.stage,
            
            c.alternate_of_code,
            c.duplicate_of_code,
            c.real_traits,
            c.customization_options,
            c.deck_options,
            c.deck_requirements,
            c.side_deck_options,
            c.side_deck_requirements,
            c.restrictions,
            c.real_text,
            c.real_back_text,
            c.real_customization_text,
            
            c.sort_by_type,
            c.sort_by_faction,
            c.sort_by_slot
        FROM requested r
        JOIN card c ON c.id = r.id
        JOIN card_type t ON c.type_code = t.code
        LEFT JOIN card_subtype st ON c.subtype_code = st.code
        JOIN faction f ON c.faction_code = f.code
        JOIN pack p ON c.pack_code = p.code
        JOIN cycle cy ON c.cycle_code = cy.code
        LEFT JOIN encounter_set e ON c.encounter_code = e.code
        ORDER BY r.position
    """.trimIndent()

internal fun buildSearchCardsQuery(searchConfig: CardSearchConfig): RoomRawQuery {
    val sortClause = buildSortClause(
        with(searchConfig) {
            if (spoiler) preferences.mythosSortOrder else preferences.playerSortOrder
        },
        searchConfig.spoiler
    )

    val filterClause = searchConfig.filters.buildFiltersQuery("candidate")

    val (packsQuery, reprintsQuery) = if (searchConfig.preferences.ignoreCollection) "" to ""
    else {
        val packs = searchConfig.preferences.collection.packs.joinToString(",") { "'$it'" }
        val reprints = searchConfig.preferences.collection.reprintPacks.joinToString(",") { "'$it'" }
        packs to reprints
    }

    val rankedQueryPart = """
            SELECT *, ROW_NUMBER() OVER (
                PARTITION BY
                    COALESCE(duplicate_of_code, code)
                ORDER BY
                    CASE
                        WHEN duplicate_of_code IS NULL THEN 0
                        ELSE 1
                    END,
                    code
            ) AS duplicate_rank FROM filtered_cards
        """.trimIndent()

    val spoilerQueryPart = if (searchConfig.spoiler) {
        "SELECT *, MIN(pack_position) OVER (" +
                "PARTITION BY encounter_code" +
                ") AS encounter_group FROM ranked_cards "
    } else "SELECT * FROM ranked_cards "

    val finalQueryPart = spoilerQueryPart + "WHERE duplicate_rank = 1" +
            if (sortClause.isNotEmpty()) " ORDER BY $sortClause" else ""

    val qlFields = getQLFields("c")
    val backQLFields = getQLFields("b", "back_")

    return RoomRawQuery(
        sql = """
                WITH filtered_cards AS (
                    WITH selected_taboo AS (
                        SELECT CASE
                            WHEN ? = 0 THEN NULL
                            WHEN ? = 100 THEN (SELECT MAX(id) FROM taboo_set)
                            ELSE ?
                        END AS id
                    )
                    
                    SELECT
                        $qlFields
                        $backQLFields
                        
                        CASE WHEN fc.code IS NULL THEN 0 ELSE 1 END AS isFavorite,
                    
                        c.duplicate_of_code,
                        c.pack_position,
                        c.encounter_position,
                        
                        c.customization_options,
                        c.deck_options,
                        c.deck_requirements,
                        c.side_deck_options,
                        c.side_deck_requirements,
                        
                        c.sort_by_type,
                        c.sort_by_faction,
                        c.sort_by_pack,
                        c.sort_by_cycle,
                        c.sort_by_slot,
                        
                        c.search_name,
                        c.search_name_back,
                        c.search_game,
                        c.search_game_back,
                        c.search_flavor,
                        c.search_flavor_back,
                        c.search_real_name,
                        c.search_real_name_back,
                        c.search_real_game,
                        c.search_real_game_back,
                        c.search_real_flavor,
                        c.search_real_flavor_back,
                    
                        b.search_name AS back_search_name,
                        b.search_name_back AS back_search_name_back,
                        b.search_game AS back_search_game,
                        b.search_game_back AS back_search_game_back,
                        b.search_flavor AS back_search_flavor,
                        b.search_flavor_back AS back_search_flavor_back,
                        b.search_real_name AS back_search_real_name,
                        b.search_real_name_back AS back_search_real_name_back,
                        b.search_real_game AS back_search_real_game,
                        b.search_real_game_back AS back_search_real_game_back,
                        b.search_real_flavor AS back_search_real_flavor,
                        b.search_real_flavor_back AS back_search_real_flavor_back
                    FROM card c
                    
                    JOIN card_type ct ON c.type_code = ct.code
                    LEFT JOIN card_subtype cst ON c.subtype_code = cst.code
                    JOIN faction cf ON c.faction_code = cf.code
                    LEFT JOIN faction cf2 ON c.faction2_code = cf2.code
                    LEFT JOIN faction cf3 ON c.faction3_code = cf3.code
                    JOIN pack cp ON c.pack_code = cp.code
                    JOIN cycle ccy ON c.cycle_code = ccy.code
                    LEFT JOIN encounter_set ce ON c.encounter_code = ce.code
                    LEFT JOIN favorite_card fc ON c.code = fc.code
                    
                    LEFT JOIN card b ON b.code = c.back_link_id
                    
                    LEFT JOIN card_type bt ON b.type_code = bt.code
                    LEFT JOIN card_subtype bst ON b.subtype_code = bst.code
                    LEFT JOIN faction bf ON b.faction_code = bf.code
                    LEFT JOIN faction bf2 ON b.faction2_code = bf2.code
                    LEFT JOIN faction bf3 ON b.faction3_code = bf3.code
                    LEFT JOIN pack bp ON b.pack_code = bp.code
                    LEFT JOIN cycle bcy ON b.cycle_code = bcy.code
                    LEFT JOIN encounter_set be ON b.encounter_code = be.code
                    
                    CROSS JOIN selected_taboo taboo
                    WHERE (c.encounter_code IS ${if (searchConfig.spoiler) "NOT NULL)" else "NULL OR c.xp IS NOT NULL)"} 
                    ${
                        when (searchConfig.filters.ownershipFilter) {
                            Ownership.All -> ""
            
                            Ownership.Collection -> {
                                if (searchConfig.filters.packs.isNotEmpty()
                                    || searchConfig.preferences.ignoreCollection) ""
                                else """ AND (
                                    c.pack_code IN ($packsQuery) 
                                    OR c.reprint_pack_code IN ($reprintsQuery)
                                )""".trimIndent()
                            }
            
                            Ownership.Unavailable -> {
                                if (searchConfig.preferences.ignoreCollection) " AND NULL "
                                else """ AND (
                                    c.pack_code NOT IN ($packsQuery) 
                                    AND (
                                        c.reprint_pack_code IS NULL
                                        OR c.reprint_pack_code NOT IN ($reprintsQuery)
                                    )
                                )""".trimIndent()
                            }
                        }
                    }
                    ${if (filterClause.isNotBlank())
                        """ AND EXISTS (
                            SELECT 1
                            FROM card candidate INDEXED BY index_card_code
                            WHERE (candidate.code = c.code OR candidate.code = c.back_link_id) 
                            AND $filterClause
                        )""".trimIndent() else ""
                    }
                    ${ if (searchConfig.spoiler || searchConfig.filters.tabooSetId != null) ""
                    else """ AND
                         (
                            -- No taboo selected -> originals only
                            (taboo.id IS NULL AND c.taboo_set_id IS NULL)
            
                            OR
                            
                            (taboo.id IS NOT NULL AND 
                                (
                                    -- Selected taboo version
                                    c.taboo_set_id = taboo.id
                    
                                    OR
                    
                                    -- Original version if no taboo override exists
                                    (c.taboo_set_id IS NULL
                                        AND NOT EXISTS (
                                            SELECT 1 FROM card t WHERE t.taboo_set_id = taboo.id AND t.code = c.code
                                        )
                                    )
                                )
                            )
                         )
                        """.trimIndent()
                    }
                     AND c.hidden = 0
                    ${ if (!searchConfig.preferences.showFanMade 
                        && searchConfig.filters.officialFilter == null) " AND c.official = 1"
                        else ""
                    }
                    ${if (searchConfig.preferences.showPreview) "" else " AND c.preview = 0"}
                ),
                
                ranked_cards AS ($rankedQueryPart)

                $finalQueryPart
            """.trimIndent(),
        onBindStatement = { statement ->
            var index = 1
            with(searchConfig.preferences) {
                repeat(3) {
                    statement.bindInt(index++, tabooSetId)
                }
            }
        }
    )
}

private fun getQLFields(
    alias: String,
    prefix: String? = null
): String {
    val columnPrefix = prefix.orEmpty()

    return """
            $alias.alternate_of_code AS ${columnPrefix}alternate_of_code,
            $alias.id AS ${columnPrefix}id,
            $alias.code AS ${columnPrefix}code,
            $alias.back_illustrator AS ${columnPrefix}backIllustrator,
            $alias.back_type AS ${columnPrefix}back_type,
            ${alias}p.chapter AS ${columnPrefix}chapter,
            $alias.clues AS ${columnPrefix}clues,
            $alias.cost AS ${columnPrefix}cost,
            $alias.cycle_code AS ${columnPrefix}cycle_code,
            ${alias}cy.name AS ${columnPrefix}cycleName,
            ${alias}cy.real_name AS ${columnPrefix}cycleRealName,
            $alias.deck_limit AS ${columnPrefix}deck_limit,
            $alias.doom AS ${columnPrefix}doom,
            $alias.duplicate_of_code AS ${columnPrefix}duplicate_of_code,
            $alias.encounter_code AS ${columnPrefix}encounter_code,
            ${alias}e.name AS ${columnPrefix}encounterName,
            ${alias}e.real_name AS ${columnPrefix}encounterRealName,
            $alias.enemy_damage AS ${columnPrefix}enemy_damage,
            $alias.enemy_horror AS ${columnPrefix}enemy_horror,
            $alias.enemy_fight AS ${columnPrefix}enemy_fight,
            $alias.enemy_evade AS ${columnPrefix}enemy_evade,
            $alias.exceptional AS ${columnPrefix}exceptional,
            $alias.exile AS ${columnPrefix}exile,
            $alias.faction_code AS ${columnPrefix}faction_code,
            ${alias}f.name AS ${columnPrefix}factionName,
            $alias.faction2_code AS ${columnPrefix}faction2_code,
            ${alias}f2.name AS ${columnPrefix}faction2Name,
            $alias.faction3_code AS ${columnPrefix}faction3_code,
            ${alias}f3.name AS ${columnPrefix}faction3Name,
            $alias.health AS ${columnPrefix}health,
            $alias.illustrator AS ${columnPrefix}illustrator,
            $alias.is_unique AS ${columnPrefix}is_unique,
            $alias.myriad AS ${columnPrefix}myriad,
            $alias.official AS ${columnPrefix}official,
            $alias.pack_code AS ${columnPrefix}pack_code,
            ${alias}p.name AS ${columnPrefix}packName,
            ${alias}p.real_name AS ${columnPrefix}packRealName,
            $alias.parallel AS ${columnPrefix}parallel,
            $alias.permanent AS ${columnPrefix}permanent,
            $alias.real_back_flavor AS ${columnPrefix}real_back_flavor,
            $alias.real_back_name AS ${columnPrefix}real_back_name,
            $alias.real_back_subname AS ${columnPrefix}real_back_subname,
            $alias.real_back_text AS ${columnPrefix}real_back_text,
            $alias.real_back_traits AS ${columnPrefix}real_back_traits,
            $alias.real_customization_text AS ${columnPrefix}real_customization_text,
            $alias.real_flavor AS ${columnPrefix}real_flavor,
            $alias.real_name AS ${columnPrefix}real_name,
            $alias.real_slot AS ${columnPrefix}real_slot,
            $alias.real_subname AS ${columnPrefix}real_subname,
            $alias.real_text AS ${columnPrefix}real_text,
            $alias.real_traits AS ${columnPrefix}real_traits,
            $alias.restrictions AS ${columnPrefix}restrictions,
            $alias.sanity AS ${columnPrefix}sanity,
            $alias.shroud AS ${columnPrefix}shroud,
            $alias.stage AS ${columnPrefix}stage,
            $alias.subtype_code AS ${columnPrefix}subtype_code,
            ${alias}st.name AS ${columnPrefix}subTypeName,
            $alias.xp AS ${columnPrefix}xp,
            $alias.vengeance AS ${columnPrefix}vengeance,
            $alias.victory AS ${columnPrefix}victory,
            $alias.quantity AS ${columnPrefix}quantity,
            $alias.type_code AS ${columnPrefix}type_code,
            ${alias}t.name AS ${columnPrefix}typeName,
            $alias.taboo_xp AS ${columnPrefix}taboo_xp,
            $alias.taboo_set_id AS ${columnPrefix}taboo_set_id,
            $alias.taboo_placeholder AS ${columnPrefix}taboo_placeholder,
            $alias.skill_willpower AS ${columnPrefix}skill_willpower,
            $alias.skill_intellect AS ${columnPrefix}skill_intellect,
            $alias.skill_combat AS ${columnPrefix}skill_combat,
            $alias.skill_agility AS ${columnPrefix}skill_agility,
            $alias.skill_wild AS ${columnPrefix}skill_wild,
            $alias.back_flavor AS ${columnPrefix}translation_back_flavor,
            $alias.back_name AS ${columnPrefix}translation_back_name,
            $alias.back_subname AS ${columnPrefix}translation_back_subname,
            $alias.back_text AS ${columnPrefix}translation_back_text,
            $alias.back_traits AS ${columnPrefix}translation_back_traits,
            $alias.flavor AS ${columnPrefix}translation_flavor,
            $alias.name AS ${columnPrefix}translation_name,
            $alias.slot AS ${columnPrefix}translation_slot,
            $alias.subname AS ${columnPrefix}translation_subname,
            $alias.text AS ${columnPrefix}translation_text,
            $alias.traits AS ${columnPrefix}translation_traits,
        """.trimIndent()
}

internal fun CardFilters.buildFiltersQuery(alias: String): String {
    val defaultFilters = CardFilters()

    if (this == defaultFilters) return ""

    val filtersListBuilder = buildList {
        var codes: MutableSet<String>? = null

        fun applyCodes(otherCodes: Set<String>) {
            if (codes == null) {
                codes = otherCodes.toMutableSet()
            } else {
                if (codes.isNotEmpty()) codes.retainAll(otherCodes)
            }
        }

        /*
        *  First build filters with indexed fields
        */

        propertiesFilter.run {
            if (this == defaultFilters.propertiesFilter) return@run

            if (fast) {
                applyCodes(CardCache.properties["fast"].orEmpty())
            }
            if (healsDamage) {
                applyCodes(CardCache.tags["hd"].orEmpty())
            }
            if (healsHorror) {
                applyCodes(CardCache.tags["hh"].orEmpty())
            }
            if (seal) {
                applyCodes(CardCache.tags["se"].orEmpty())
            }
            if (succeedBy) {
                applyCodes(CardCache.properties["succeeds_by"].orEmpty())
            }
        }

        assetFilter.run {
            if (this == defaultFilters.assetFilter) return@run

            if (skillBoosts.isNotEmpty()) {
                applyCodes(
                    skillBoosts.flatMap {
                        CardCache.skillBoosts[it].orEmpty()
                    }.toSet()
                )
            }

            if (uses.isNotEmpty()) {
                applyCodes(
                    uses.flatMap {
                        CardCache.uses[it].orEmpty()
                    }.toSet()
                )
            }

            if (slots.isNotEmpty()) {
                applyCodes(
                    slots.flatMap {
                        CardCache.slots[it].orEmpty()
                    }.toSet()
                )
            }
        }

        if (actions.isNotEmpty()) {
            applyCodes(
                actions.flatMap {
                    CardCache.actions[it].orEmpty()
                }.toSet()
            )
        }

        if (traits.isNotEmpty()) {
            applyCodes(
                traits.flatMap {
                    CardCache.traits[it].orEmpty()
                }.toSet()
            )
        }

        codes?.let { codes ->
            if (codes.isEmpty()) {
                add("1 = 0")
                return@buildList
            }
            val cardCodesString = codes.joinToString(",") { "'$it'" }
            add("${alias}.code IN ($cardCodesString)")
        }

        if (factions.isNotEmpty()) {
            val factionsString = factions.joinToString(",") { "'${it.name.lowercase()}'" }
            add("""
                    (
                        ${alias}.faction_code IN ($factionsString) 
                        OR ${alias}.faction2_code IN ($factionsString) 
                        OR ${alias}.faction3_code IN ($factionsString)
                    )
                """.trimIndent())
        }

        if (types.isNotEmpty()) {
            val typesString = types.joinToString(",") { "'${it.code}'" }
            add("${alias}.type_code IN ($typesString)")
        }

        if (subTypes.isNotEmpty()) {
            val nonNullable = subTypes.filterNotNull()
            val haveNull = null in subTypes
            val nonNullableString = nonNullable.joinToString(",") { "'${it.name.lowercase()}'" }
            add("""
                    (
                        ${if (haveNull) "${alias}.subtype_code IS NULL" else ""}
                        ${if (nonNullable.isNotEmpty()) {
                (if (haveNull) " OR " else "") +
                        "${alias}.subtype_code IN ($nonNullableString)"
            } else ""}
                    )
                """.trimIndent())

        }

        if (encounterSets.isNotEmpty()) {
            val encounterSetsString = encounterSets.joinToString(",") { "'$it'" }
            add("${alias}.encounter_code IN ($encounterSetsString)")
        }

        packs.run {
            if (isEmpty()) return@run

            val packsString = packs.joinToString(",") { "'$it'" }
            val reprintsString = reprintPacks.joinToString(",") { "'$it'" }
            add("""
                    (
                        ${alias}.pack_code IN ($packsString) 
                        OR ${alias}.reprint_pack_code IN ($reprintsString)
                    )
                """.trimIndent())
        }

        tabooSetId?.let {
            add("(c.taboo_set_id = $it AND c.taboo_placeholder = 0)")
        }

        /*
        *  Non-indexed filters
        */

        cardpoolFilter?.let {
            val investigatorId = it.investigatorConfig.investigatorId

            add("""
                    (
                        ${if (it.showInvestigator) """
                            (
                                c.code = '$investigatorId' 
                                OR c.duplicate_of_code = '$investigatorId'
                            ) OR 
                        """.trimIndent() else ""}
                        (
                            EXISTS (
                                SELECT 1 FROM card c2 
                                WHERE c2.code = c.code 
                                    AND c2.taboo_set_id IS NULL 
                                    AND c2.deck_limit > 0
                            ) 
                            AND c.type_code != 'investigator'
                        )
                    )
                """.trimIndent())
        }

        if (whoCanTakeCard.isNotEmpty()) {
            add("(c.type_code == 'investigator' AND c.deck_options IS NOT NULL)")
        }

        favoritesOnly.run {
            if (!this) return@run

            add("isFavorite = 1")
        }

        levelFilter.run {
            if (this == defaultFilters.levelFilter) return@run

            val result = forcedRange ?: range
            val (min, max) = result
            add(
                when {
                    max == null -> "(${alias}.xp IS NULL)"
                    min == null -> "(${alias}.xp IS NULL OR ${alias}.xp <= $max)"
                    else -> "(${alias}.xp BETWEEN $min AND $max)"
                }
            )
        }

        costFilter.run {
            if (this == defaultFilters.costFilter) return@run

            val (min, max) = range
            add("""
                    (
                        ${if (xCost) "${alias}.cost = -2 OR " else ""}
                        (
                            (${
                when {
                    max == null -> "${alias}.cost IS NULL"
                    min == null -> "${alias}.cost IS NULL OR ${alias}.cost <= $max"
                    else -> "${alias}.cost BETWEEN $min AND $max"
                }
            }) 
                            ${if (evenCost || oddCost) """
                                AND ${alias}.cost % 2 = ${if (evenCost) "0" else "1"}
                            """.trimIndent() else ""}
                        )
                    )
                """.trimIndent())
        }

        skillsFilter.run {
            if (this == defaultFilters.skillsFilter) return@run

            add("(${alias}.type_code != 'investigator')")
            willpower?.let { add("${alias}.skill_willpower >= $it") }
            intellect?.let { add("${alias}.skill_intellect >= $it") }
            combat?.let { add("${alias}.skill_combat >= $it") }
            agility?.let { add("${alias}.skill_agility >= $it") }
            wild?.let { add("${alias}.skill_wild >= $it") }
            any?.let {
                val anyList = buildList {
                    if (willpower == null) add("${alias}.skill_willpower >= $it")
                    if (intellect == null) add("${alias}.skill_intellect >= $it")
                    if (combat == null) add("${alias}.skill_combat >= $it")
                    if (agility == null) add("${alias}.skill_agility >= $it")
                    if (wild == null) add("${alias}.skill_wild >= $it")
                }

                if (anyList.isNotEmpty()) {
                    add("(${anyList.joinToString(" OR ")})")
                }
            }
        }

        healthSanityFilter.run {
            if (this == defaultFilters.healthSanityFilter) return@run

            health.run {
                add("""
                        (
                            ${if (includeXHealthOrSanity) "${alias}.health = -2 OR " else ""}
                            (${
                    when {
                        max == null -> "${alias}.health IS NULL OR ${alias}.health = 0"
                        min == null -> "${alias}.health IS NULL OR ${alias}.health = 0 " +
                                "OR ${alias}.health <= $max"
                        else -> "${alias}.health BETWEEN $min AND $max"
                    } + if (healthPerInvestigator) " AND ${alias}.health_per_investigator = 1" else ""
                })
                        )
                    """.trimIndent())
            }

            sanity.run {
                add("""
                        (
                            ${if (includeXHealthOrSanity) "${alias}.sanity = -2 OR " else ""}
                            (${
                    when {
                        max == null -> "${alias}.sanity IS NULL OR ${alias}.sanity = 0"
                        min == null -> "${alias}.sanity IS NULL OR ${alias}.sanity = 0 " +
                                "OR ${alias}.sanity <= $max"
                        else -> "${alias}.sanity BETWEEN $min AND $max"
                    }
                })
                        )
                    """.trimIndent())
            }
        }

        propertiesFilter.run {
            if (this == defaultFilters.propertiesFilter) return@run

            if (customizable) add("${alias}.customization_text IS NOT NULL")
            if (exile) add("${alias}.exile = 1")
            if (exceptional) add("c.exceptional = 1")
            if (multiclass) add("${alias}.faction2_code IS NOT NULL")
            if (myriad) add("${alias}.myriad = 1")
            if (permanent) add("${alias}.permanent = 1")
            if (specialist) add("${alias}.restrictions LIKE '{\"trait\":%'")
            if (unique) add("${alias}.is_unique = 1")
            if (victory) add("${alias}.victory IS NOT NULL")
        }

        enemyFilter.run {
            if (this == defaultFilters.enemyFilter) return@run

            add("(${alias}.type_code = 'enemy' OR ${alias}.type_code = 'enemy_location')")

            fight.run {
                add("""
                        (${
                    when {
                        max == null -> "${alias}.enemy_fight IS NULL"
                        min == null -> "${alias}.enemy_fight IS NULL OR ${alias}.enemy_fight <= $max"
                        else -> "${alias}.enemy_fight BETWEEN $min AND $max"
                    }
                })
                    """.trimIndent())
            }

            evade.run {
                add("""
                        (${
                    when {
                        max == null -> "${alias}.enemy_evade IS NULL"
                        min == null -> "${alias}.enemy_evade IS NULL OR ${alias}.enemy_evade <= $max"
                        else -> "${alias}.enemy_evade BETWEEN $min AND $max"
                    }
                })
                    """.trimIndent())
            }

            damage.run {
                add("""
                        (${
                    when {
                        max == null -> "${alias}.enemy_damage IS NULL OR ${alias}.enemy_damage = 0"
                        min == null -> "${alias}.enemy_damage IS NULL OR ${alias}.enemy_damage = 0 " +
                                "OR ${alias}.enemy_damage <= $max"
                        else -> "${alias}.enemy_damage BETWEEN $min AND $max"
                    }
                })
                    """.trimIndent())
            }

            horror.run {
                add("""
                        (${
                    when {
                        max == null -> "${alias}.enemy_horror IS NULL OR ${alias}.enemy_horror = 0"
                        min == null -> "${alias}.enemy_horror IS NULL OR ${alias}.enemy_horror = 0 " +
                                "OR ${alias}.enemy_horror <= $max"
                        else -> "${alias}.enemy_horror BETWEEN $min AND $max"
                    }
                })
                    """.trimIndent())
            }

            if (vengeance) add("${alias}.vengeance IS NOT NULL")
        }

        locationFilter.run {
            if (this == defaultFilters.locationFilter) return@run

            add("(${alias}.type_code = 'location' OR ${alias}.type_code = 'enemy_location')")

            shroud.run {
                add("""
                        (
                            ${if (xShroud) "${alias}.shroud = -2 OR " else ""}
                            (${
                    when {
                        max == null -> "${alias}.shroud IS NULL"
                        min == null -> "${alias}.shroud IS NULL OR ${alias}.shroud <= $max"
                        else -> "${alias}.shroud BETWEEN $min AND $max"
                    }
                })
                        )
                    """.trimIndent())
            }

            clues.run {
                add("""
                        (${
                    when {
                        max == null -> "${alias}.clues IS NULL"
                        min == null -> "${alias}.clues IS NULL OR ${alias}.clues <= $max"
                        else -> "${alias}.clues BETWEEN $min AND $max"
                    } + if (perInvestigatorClues) " AND ${alias}.clues_fixed = 0" else ""
                })
                    """.trimIndent())
            }
        }

        officialFilter?.let { official ->
            if (official) add("${alias}.official = 1")
            else add("${alias}.official = 0")
        }

        if (illustrators.isNotEmpty()) {
            val illustratorsString = illustrators.joinToString(",") { "'$it'" }
            add(
                "(${alias}.illustrator IN ($illustratorsString) OR " +
                        "${alias}.back_illustrator IN ($illustratorsString))"
            )
        }
    }

    return filtersListBuilder.joinToString(" AND ")
}