package com.arkhamcompanion.data.utils

import androidx.room3.RoomRawQuery
import com.arkhamcompanion.data.local.dao.DecksDao.Companion.DECK_TOTAL_XP_QUERY
import com.arkhamcompanion.data.objects.SortQueryBuilder.buildDecksSortClause
import com.arkhamcompanion.domain.enums.DeckSource
import com.arkhamcompanion.domain.model.decks.DeckFilters

internal fun buildDecksListItemsQuery(sort: String, filters: DeckFilters): RoomRawQuery {
    val sortClause = buildDecksSortClause(sort)
    val filtersClause = filters.buildFiltersQuery()

    return RoomRawQuery(
        sql = """
                WITH RECURSIVE

                current AS (
                    SELECT *
                    FROM deck
                    WHERE next_deck IS NULL
                ),
                
                $DECK_TOTAL_XP_QUERY,
            
                -- Find the appropriate investigator card
                investigator_candidates AS (
                    SELECT
                        *,
                        ROW_NUMBER() OVER (
                            PARTITION BY deckId
                            ORDER BY sourceRank, tabooRank
                        ) AS investigatorRank
                    FROM (
                        -- Official cards
                        SELECT
                            d.id AS deckId,
                            i.name,
                            i.parallel,
                            i.faction_code,
                            i.faction2_code,
                            i.faction3_code,
                            i.thumbnailurl,
                            i.imageurl,

                            1 AS sourceRank,

                            CASE
                                WHEN i.taboo_set_id = d.taboo_id THEN 0
                                ELSE 1
                            END AS tabooRank

                        FROM current d
                        INNER JOIN card i
                            ON i.code = COALESCE(
                                d.transform_into,
                                d.alternate_front_code,
                                d.investigator_code
                            )
                            AND (
                                i.taboo_set_id = d.taboo_id
                                OR i.taboo_set_id IS NULL
                            )

                        UNION ALL

                        -- Fan-made investigator
                        SELECT
                            d.id AS deckId,
                            json_extract(fc.value, '$.name') AS name,
                            COALESCE(json_extract(fc.value, '$.parallel'), 0) AS parallel,
                            json_extract(fc.value, '$.faction_code') AS faction_code,
                            json_extract(fc.value, '$.faction2_code') AS faction2_code,
                            json_extract(fc.value, '$.faction3_code') AS faction3_code,
                            json_extract(fc.value, '$.thumbnail_url') AS thumbnailurl,
                            json_extract(fc.value, '$.image_url') AS imageurl,

                            0 AS sourceRank,
                            0 AS tabooRank

                        FROM current d

                        INNER JOIN json_each(
                            COALESCE(d.meta, '{}'),
                            '$.fan_made_content.cards'
                        ) fc
                            ON fc.key = COALESCE(
                                d.transform_into,
                                d.alternate_front_code,
                                d.investigator_code
                            )
                    )
                )
            
                SELECT
                    d.id, 
                    d.name, 
                    d.problem, 
                    d.slots, 
                    d.extra_deck, 
                    d.card_pool, 
                    d.source, 
                    d.tags, 
                    d.xp_adjustment, 
                    d.xp_spent, 
                    d.xp,
            
                    i.name AS investigatorName,
                    i.parallel AS investigatorParallel,
                    i.faction_code AS investigatorFaction,
                    i.faction2_code AS investigatorFaction2,
                    i.faction3_code AS investigatorFaction3,
                    i.thumbnailurl AS investigatorThumbnailUrl,
                    i.imageurl AS investigatorImageUrl,
            
                    COALESCE(dx.totalXp, 0) + 
                    COALESCE(cx.totalCustomXp, 0) AS totalXp
            
                FROM current d
            
                LEFT JOIN investigator_candidates i ON i.deckId = d.id
                    AND i.investigatorRank = 1
            
                LEFT JOIN deck_xp dx ON dx.deckId = d.id
                    
                LEFT JOIN custom_deck_xp cx ON cx.deckId = d.id
                
                ${if (filtersClause.isNotBlank()) "WHERE $filtersClause " else ""}
            
                ORDER BY $sortClause
            """.trimIndent()
    )
}

private fun DeckFilters.buildFiltersQuery(): String {
    val defaultFilters = DeckFilters()

    if (this == defaultFilters) return ""

    val filtersListBuilder = buildList {
        if (factions.isNotEmpty()) {
            val factionsString = factions.joinToString(",") { "'${it.name.lowercase()}'" }
            add(
                """
                    (
                        i.faction_code IN ($factionsString) 
                        OR i.faction2_code IN ($factionsString) 
                        OR i.faction3_code IN ($factionsString)
                    )
                """.trimIndent()
            )
        }

        if (alternateInvestigatorsOnly) {
            add("i.parallel = 1")
        }

        if (source.isNotEmpty()) {
            val sourceString = source.joinToString(",") {
                val name = when (it) {
                    DeckSource.Device -> null
                    else -> it.name.lowercase()
                }

                "'${name}'"
            }
            add("d.source IN ($sourceString)")
        }

        if (tags.isNotEmpty()) {
            add(
                tags.joinToString(" OR ", prefix = "(", postfix = ")") {
                    """
                        instr(
                            ' ' || d.tags || ' ',
                            ' ' || '$it' || ' '
                        ) > 0
                    """.trimIndent()
                }
            )
        }

        if (cardsInDeck.isNotEmpty()) {
            add(
                cardsInDeck.joinToString(" AND ", prefix = "(", postfix = ")") {
                    """(
                        EXISTS (
                            SELECT 1
                            FROM json_each(d.slots) AS s
                            WHERE s.key = '${it.code}'
                        )
                        OR
                        EXISTS (
                            SELECT 1
                            FROM json_each(d.side_slots) AS ss
                            WHERE ss.key = '${it.code}'
                        )
                        OR
                        instr(
                            ',' || replace(d.extra_deck, ' ', '') || ',',
                            ',' || '${it.code}' || ','
                        ) > 0
                    )""".trimIndent()
                }
            )
        }
    }

    return filtersListBuilder.joinToString(" AND ")
}