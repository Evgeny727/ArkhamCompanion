package com.arkhamcompanion.data.local.cards.patches

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun loadDeckOptionsPatches(): List<CardPatch> = listOf(
    CardPatch(
        code = "05002",
        values = EntityPatch(
            deckOptions = PatchValue.Set(buildJsonArray {
                add(buildJsonObject {
                    put("atleast", buildJsonObject {
                        put("min", 11)
                        put("traits", 1)
                    })
                    put("trait", buildJsonArray {
                        add("Insight")
                    })
                    put("virtual", true)
                    put("error", "Deck must have at least 11 Insights.")
                })

                add(buildJsonObject {
                    put("faction", buildJsonArray {
                        add("seeker")
                        add("neutral")
                    })
                    put("level", buildJsonObject {
                        put("min", 0)
                        put("max", 5)
                    })
                })

                add(buildJsonObject {
                    put("faction", buildJsonArray {
                        add("guardian")
                    })
                    put("level", buildJsonObject {
                        put("min", 0)
                        put("max", 2)
                    })
                })
            }),
        )
    ),
    CardPatch(
        code = "06167",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", 5)
            }),
            deckOptions = PatchValue.Set(buildJsonArray {
                add(buildJsonObject {
                    put("name", "Versatile")
                    put("level", buildJsonObject {
                        put("min", 0)
                        put("max", 0)
                    })
                    put("limit", 1)
                    put("error", "Too many off-class cards for Versatile.")
                })
            }),
        )
    ),
    CardPatch(
        code = "07303",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", 5)
            }),
            deckOptions = PatchValue.Set(buildJsonArray {
                add(buildJsonObject {
                    put("atleast", buildJsonObject {
                        put("min", 10)
                        put("types", 1)
                    })
                    put("type", buildJsonArray {
                        add("skill")
                    })
                    put("virtual", true)
                    put("error", "Deck must have at least 10 skill cards.")
                })
            }),
        )
    ),
    CardPatch(
        code = "08046",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", -5)
            }),
        )
    ),
    CardPatch(
        code = "08031",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", 15)
            }),
        )
    ),
    CardPatch(
        code = "09077",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", 10)
            }),
        )
    ),
    CardPatch(
        code = "53010",
        values = EntityPatch(
            deckOptions = PatchValue.Set(buildJsonArray {
                add(buildJsonObject {
                    put("not", true)
                    put("slot", buildJsonArray {
                        add("Ally")
                    })
                    put("level", buildJsonObject {
                        put("min", 0)
                        put("max", 5)
                    })
                    put("virtual", true)
                    put("error", "You cannot have assets that take up an ally slot.")
                })
            }),
        )
    ),
    CardPatch(
        code = "12181",
        values = EntityPatch(
            deckRequirements = PatchValue.Set(buildJsonObject {
                put("size", 5)
            }),
            deckOptions = PatchValue.Set(buildJsonArray {
                add(buildJsonObject {
                    put("name", "Collector")
                    put("level", buildJsonObject {
                        put("min", 0)
                        put("max", 3)
                    })
                    put("trait", buildJsonArray {
                        add("relic")
                        add("charm")
                    })
                    put("type", buildJsonArray {
                        add("asset")
                    })
                    put("limit", 1)
                    put("error", "Too many off-class cards for Collector.")
                })
            }),
        )
    ),
)