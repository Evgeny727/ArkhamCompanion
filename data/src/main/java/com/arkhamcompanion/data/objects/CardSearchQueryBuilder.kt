package com.arkhamcompanion.data.objects

object CardSearchQueryBuilder {
    fun buildSortClause(sortOrder: List<String>, spoiler: Boolean): String {
        val positionFirst = sortOrder.indexOf("position") == 0
        return sortOrder.joinToString(", ") { option ->
            val col = when(option) {
                "type" -> "sort_by_type"
                "slot" -> "sort_by_slot"
                "faction" -> "sort_by_faction"
                "cost" -> "(cost IS NULL), cost"
                "level" -> "(xp IS NULL), xp"
                "cycle" -> "sort_by_cycle"
                "pack" -> "sort_by_pack"
                "name" -> "translation_name"
                "position" -> (if (positionFirst) "sort_by_pack, " else "") +
                        (if (spoiler) "encounter_group, encounter_code, " else "") + "pack_position"
                "encounter" -> "encounter_code, encounter_position"
                else -> option
            }
            col
        }
    }
}