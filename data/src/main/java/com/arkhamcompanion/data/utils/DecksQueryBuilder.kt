package com.arkhamcompanion.data.utils

import androidx.room3.RoomRawQuery
import com.arkhamcompanion.data.objects.SortQueryBuilder.buildDecksSortClause

internal fun buildDecksListItemsQuery(sort: String): RoomRawQuery {
    val sortClause = buildDecksSortClause(sort)

    return RoomRawQuery(
        sql = """
                WITH RECURSIVE

                current_decks AS (
                    SELECT *
                    FROM deck
                    WHERE next_deck IS NULL
                ),
            
                -- Split extra_deck into individual card codes
                extra_deck_parts AS (
                    SELECT
                        id AS deckId,
                        TRIM(
                            SUBSTR(extra_deck || ',', 1,
                                INSTR(extra_deck || ',', ',') - 1)
                        ) AS cardCode,
                        SUBSTR(
                            extra_deck || ',',
                            INSTR(extra_deck || ',', ',') + 1
                        ) AS remaining
                    FROM current_decks
                    WHERE extra_deck IS NOT NULL
                      AND TRIM(extra_deck) != ''
            
                    UNION ALL
            
                    SELECT
                        deckId,
                        TRIM(SUBSTR(remaining, 1, INSTR(remaining, ',') - 1)),
                        SUBSTR(remaining, INSTR(remaining, ',') + 1)
                    FROM extra_deck_parts
                    WHERE remaining != ''
                ),
            
                -- Extract cards from slots and extra_deck
                deck_cards AS (
                    SELECT
                        d.id AS deckId,
                        j.key AS cardCode,
                        CAST(j.value AS INTEGER) AS quantity
                    FROM current_decks d,
                         json_each(d.slots) j
            
                    UNION ALL
            
                    SELECT
                        deckId,
                        cardCode,
                        1
                    FROM extra_deck_parts
                    WHERE cardCode != ''
                ),
            
                -- Combine quantities of identical card codes
                card_counts AS (
                    SELECT
                        deckId,
                        cardCode,
                        SUM(quantity) AS quantity
                    FROM deck_cards
                    GROUP BY deckId, cardCode
                ),
            
                -- Find the appropriate taboo version of every card
                card_candidates AS (
                    SELECT
                        cc.deckId,
                        cc.cardCode,
                        cc.quantity,
                        c.real_name,
                        COALESCE(c.myriad, 0) AS myriad,
                        COALESCE(c.xp, 0) *
                            CASE WHEN c.exceptional = 1 THEN 2 ELSE 1 END
                            + COALESCE(c.taboo_xp, 0) AS unitXp,
            
                        ROW_NUMBER() OVER (
                            PARTITION BY cc.deckId, cc.cardCode
                            ORDER BY
                                CASE
                                    WHEN c.taboo_set_id = d.taboo_id THEN 0
                                    ELSE 1
                                END
                        ) AS versionRank
            
                    FROM card_counts cc
                    INNER JOIN current_decks d
                        ON d.id = cc.deckId
                    INNER JOIN card c
                        ON c.code = cc.cardCode
                        AND (
                            c.taboo_set_id = d.taboo_id
                            OR c.taboo_set_id IS NULL
                        )
                ),
            
                -- Keep only the preferred card version
                card_costs AS (
                    SELECT *
                    FROM card_candidates
                    WHERE versionRank = 1
                ),
            
                -- Deduplicate Myriad cards
                ranked_cards AS (
                    SELECT
                        *,
                        ROW_NUMBER() OVER (
                            PARTITION BY
                                deckId,
                                CASE
                                    WHEN myriad = 1
                                        THEN 'M:' || COALESCE(real_name, cardCode)
                                    ELSE 'C:' || cardCode
                                END
                            ORDER BY cardCode
                        ) AS rn
                    FROM card_costs
                ),
            
                -- Calculate XP for each deck
                deck_xp AS (
                    SELECT
                        deckId,
                        SUM(
                            CASE
                                WHEN myriad = 1 THEN
                                    CASE WHEN rn = 1 THEN unitXp ELSE 0 END
                                ELSE
                                    unitXp * quantity
                            END
                        ) AS totalXp
                    FROM ranked_cards
                    GROUP BY deckId
                ),
                
                -- Split customisable card entries from meta
                custom_xp_parts AS (
                    SELECT
                        d.id AS deckId,
                        TRIM(
                            SUBSTR(
                                CAST(j.value AS TEXT) || ',',
                                1,
                                INSTR(CAST(j.value AS TEXT) || ',', ',') - 1
                            )
                        ) AS entry,
                        SUBSTR(
                            CAST(j.value AS TEXT) || ',',
                            INSTR(CAST(j.value AS TEXT) || ',', ',') + 1
                        ) AS remaining
                    FROM current_decks d,
                         json_each(COALESCE(d.meta, '{}')) j
                    WHERE SUBSTR(j.key, 1, 4) = 'cus_'
                      AND j.value IS NOT NULL
                      AND TRIM(CAST(j.value AS TEXT)) != ''
        
                    UNION ALL
        
                    SELECT
                        deckId,
                        TRIM(SUBSTR(remaining, 1, INSTR(remaining, ',') - 1)),
                        SUBSTR(remaining, INSTR(remaining, ',') + 1)
                    FROM custom_xp_parts
                    WHERE remaining != ''
                ),
        
                -- Calculate XP spent on customisable cards
                custom_deck_xp AS (
                    SELECT
                        deckId,
                        SUM(
                            CAST(
                                SUBSTR(
                                    entry,
                                    INSTR(entry, '|') + 1,
                                    INSTR(
                                        SUBSTR(entry, INSTR(entry, '|') + 1) || '|',
                                        '|'
                                    ) - 1
                                ) AS INTEGER
                            )
                        ) AS totalCustomXp
                    FROM custom_xp_parts
                    WHERE INSTR(entry, '|') > 0
                    GROUP BY deckId
                ),
            
                -- Find the appropriate investigator card
                investigator_candidates AS (
                    SELECT
                        d.id AS deckId,
                        i.name,
                        i.parallel,
                        i.faction_code,
                        i.faction2_code,
                        i.faction3_code,
                        i.thumbnailurl,
                        i.imageurl,
            
                        ROW_NUMBER() OVER (
                            PARTITION BY d.id
                            ORDER BY
                                CASE
                                    WHEN i.taboo_set_id = d.taboo_id THEN 0
                                    ELSE 1
                                END
                        ) AS investigatorRank
            
                    FROM current_decks d
                    INNER JOIN card i
                        ON i.code = COALESCE(
                            d.alternate_front_code,
                            d.investigator_code
                        )
                        AND (
                            i.taboo_set_id = d.taboo_id
                            OR i.taboo_set_id IS NULL
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
            
                FROM current_decks d
            
                LEFT JOIN investigator_candidates i ON i.deckId = d.id
                    AND i.investigatorRank = 1
            
                LEFT JOIN deck_xp dx ON dx.deckId = d.id
                    
                LEFT JOIN custom_deck_xp cx ON cx.deckId = d.id
            
                ORDER BY $sortClause
            """.trimIndent()
    )
}