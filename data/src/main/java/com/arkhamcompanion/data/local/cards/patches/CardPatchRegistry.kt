package com.arkhamcompanion.data.local.cards.patches

class CardPatchRegistry {

    private fun loadPatches(): List<CardPatch> = loadAdditionalPatches() + loadGameBeginAttributePatches() +
            loadHiddenFixesPatches() + loadMissingTagsPatches() + loadPerInvestigatorPatches() +
            loadReprintsPatches() + loadBackTypePatches() + loadDeckOptionsPatches()
    private val patchesByCode: Map<String, CardPatch> =
        loadPatches().groupBy { it.code }
            .mapValues { (_, list) -> list.reduce(CardPatch::merge) }

    fun resolve(code: String): CardPatch = patchesByCode[code] ?: CardPatch(code)
}