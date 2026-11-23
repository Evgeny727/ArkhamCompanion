package com.arkhamcompanion.data.local.cards.patches

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun loadCardAttachmentsPatches(): List<CardPatch> = listOf(
    CardPatch(
        code = "03264",
        values = EntityPatch(
            attachments = PatchValue.Set(buildJsonObject {
                put("limit", 1)
                put("traits", buildJsonArray {
                    add("tactic")
                    add("supply")
                })
                put("filters", buildJsonArray {
                    add(buildJsonObject {
                        put("attribute", "type_code")
                        put("value", "event")
                    })
                })
                put("icon", "package")
                put("targetSize", 3)
            }),
        )
    ),
    CardPatch(
        code = "05002",
        values = EntityPatch(
            attachments = PatchValue.Set(buildJsonObject {
                put("traits", buildJsonArray {
                    add("insight")
                })
                put("filters", buildJsonArray {
                    add(buildJsonObject {
                        put("attribute", "type_code")
                        put("value", "event")
                    })
                })
                put("name", "Hunch deck")
                put("icon", "lightbulb")
                put("targetSize", 11)
                put("requiredCards", buildJsonObject {
                    put("05010", 1)
                })
            }),
        )
    ),
    CardPatch(
        code = "09077",
        values = EntityPatch(
            attachments = PatchValue.Set(buildJsonObject {
                put("traits", buildJsonArray {
                    add("illicit")
                })
                put("name", "Market deck")
                put("icon", "store")
                put("targetSize", 10)
            }),
        )
    ),
    CardPatch(
        code = "10079",
        values = EntityPatch(
            attachments = PatchValue.Set(buildJsonObject {
                put("limit", 1)
                put("traits", buildJsonArray {
                    add("trick")
                })
                put("icon", "wand")
                put("targetSize", 3)
            }),
        )
    ),
    CardPatch(
        code = "11080",
        values = EntityPatch(
            attachments = PatchValue.Set(buildJsonObject {
                put("traits", buildJsonArray {
                    add("spell")
                })
                put("filters", buildJsonArray {
                    add(buildJsonObject {
                        put("attribute", "type_code")
                        put("value", "asset")
                    })
                    add(buildJsonObject {
                        put("attribute", "xp")
                        put("value", null)
                        put("not", true)
                    })
                })
                put("icon", "stamp")
                put("limit", 1)
                put("targetSize", 1)
            }),
        )
    ),
)