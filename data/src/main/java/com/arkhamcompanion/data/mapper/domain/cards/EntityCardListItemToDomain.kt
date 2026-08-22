package com.arkhamcompanion.data.mapper.domain.cards

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.arkhamcompanion.data.local.cards.CardListItemEntity
import com.arkhamcompanion.domain.enums.CardSubType
import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.enums.Faction
import com.arkhamcompanion.domain.model.cards.CardListItem
import com.arkhamcompanion.domain.model.cards.CardListItemUiModel
import com.arkhamcompanion.domain.model.cards.CardsHeaderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun CardListItemEntity.toDomain() = CardListItem(
    id = id,
    code = code,
    thumbnailUrl = thumbnailurl,
    realCost = cost.realCardCost(typeCode, permanent),
    cost = cost,
    xp = xp,
    permanent = permanent,
    tabooXp = tabooXp,
    tabooSetId = tabooSetId,
    tabooPlaceholder = tabooPlaceholder,
    type = CardType.byType(typeCode),
    typeName = typeName,
    typeNumber = typeNumber,
    subType = subTypeCode?.let { CardSubType.bySubType(subTypeCode) },
    subTypeName = subTypeName,
    faction = Faction.byFaction(factionCode),
    faction2 = faction2Code?.let { Faction.byFaction(faction2Code) },
    faction3 = faction3Code?.let { Faction.byFaction(faction3Code) },
    factionNumber = factionNumber,
    slot = slot,
    slotNumber = slotNumber,
    packCode = packCode,
    packPosition = packPosition,
    packName = packName,
    cycleCode = cycleCode,
    cycleName = cycleName,
    cyclePosition = cyclePosition,
    encounterCode = encounterCode,
    encounterName = encounterName,
    reprintPackCode = reprintPackCode,
    name = name,
    subname = subname,
    skillWillpower = skills.skillWillpower,
    skillIntellect = skills.skillIntellect,
    skillCombat = skills.skillCombat,
    skillAgility = skills.skillAgility,
    skillWild = skills.skillWild,
    parallel = parallel,
    isUnique = isUnique,
    stage = stage
)

internal fun Int?.realCardCost(typeCode: String?, permanent: Boolean): String? {
    if (typeCode != "asset" && typeCode != "event") return null
    if (this == -2) return "X"
    if (permanent || this == null) return "-"
    return this.toString()
}

internal fun Flow<PagingData<CardListItemEntity>>.withCategoryHeaders(
    sortOrder: List<String>,
    spoiler: Boolean
): Flow<PagingData<CardListItemUiModel>> = map { pagingData ->
    pagingData.map { item ->
        CardListItemUiModel.CardItem(item.toDomain())
    }
    .insertSeparators { before, after ->
        val beforeItem = before?.card
        val afterItem = after?.card ?: return@insertSeparators null

        val headerOptions = getHeaderOptions(
            sortOrder = sortOrder,
            spoiler = spoiler,
            previousItem = beforeItem,
            nextItem = afterItem
        )

        if (headerOptions.first) {
            val value = with(afterItem) {
                //Show all cycles made just for data integrity as packs, not cycles
                val resolvedPackName = if (cyclePosition == 13 || cyclePosition < 50) cycleName else packName

                when(headerOptions.second) {
                    CardsHeaderType.TYPE -> subTypeName ?: typeName
                    CardsHeaderType.TYPE_SLOT -> (subTypeName ?: typeName) to
                            (if (slotNumber in 1..100) {
                                if (slotNumber in 16..98) slot else slotNumber
                            } else null)
                    CardsHeaderType.SLOT -> if (slotNumber in 16..98) slot else slotNumber
                    CardsHeaderType.FACTION -> factionNumber
                    CardsHeaderType.FACTION_PACK -> factionNumber to resolvedPackName
                    CardsHeaderType.FACTION_LEVEL -> factionNumber to xp
                    CardsHeaderType.COST -> cost
                    CardsHeaderType.LEVEL -> xp
                    CardsHeaderType.CYCLE -> cycleName
                    CardsHeaderType.PACK -> resolvedPackName
                    CardsHeaderType.PACK_ENCOUNTER_SET -> "$resolvedPackName:$encounterName"
                    CardsHeaderType.POSITION -> resolvedPackName
                    CardsHeaderType.ENCOUNTER_SET -> encounterName
                    else -> null
                }
            }
            CardListItemUiModel.CategoryHeader(
                key = "header_${headerOptions.second?.name ?: "all_cards"}_${value}_${afterItem.id}",
                firstCardCode = afterItem.code,
                category = headerOptions.second,
                value = value
            )
        }
        else null
    }
}

//Null type is 'All cards' header
private fun getHeaderOptions(
    sortOrder: List<String>,
    spoiler: Boolean,
    previousItem: CardListItem?,
    nextItem: CardListItem
): Pair<Boolean, CardsHeaderType?> {
    return when(sortOrder.first()) {
        "type" -> {
            val showType = previousItem?.typeNumber != nextItem.typeNumber

            if (sortOrder.getOrNull(1) == "slot") {
                (showType || previousItem.slotNumber != nextItem.slotNumber) to CardsHeaderType.TYPE_SLOT
            }
            else showType to CardsHeaderType.TYPE
        }
        "slot" -> {
            (previousItem?.slotNumber != nextItem.slotNumber) to CardsHeaderType.SLOT
        }
        "faction" -> {
            val showFaction = previousItem?.factionNumber != nextItem.factionNumber

            if (sortOrder.getOrNull(1) == "pack") {
                val showPack = (if (nextItem.reprintPackCode != null)
                    previousItem?.reprintPackCode != nextItem.reprintPackCode
                else previousItem?.packCode != nextItem.packCode)

                (showFaction || showPack) to CardsHeaderType.FACTION_PACK
            }
            else if (sortOrder.getOrNull(1) == "level") {
                (showFaction || previousItem.xp != nextItem.xp) to CardsHeaderType.FACTION_LEVEL
            }
            else showFaction to CardsHeaderType.FACTION
        }
        "cost" -> {
            (previousItem?.cost != nextItem.cost) to CardsHeaderType.COST
        }
        "level" -> {
            (previousItem?.xp != nextItem.xp) to CardsHeaderType.LEVEL
        }
        "cycle" -> {
            (previousItem?.cycleCode != nextItem.cycleCode) to CardsHeaderType.CYCLE
        }
        "pack" -> {
            val showPack = (if (nextItem.reprintPackCode != null)
                    previousItem?.reprintPackCode != nextItem.reprintPackCode
                else previousItem?.packCode != nextItem.packCode)

            if (spoiler) {
                (showPack || previousItem?.encounterCode != nextItem.encounterCode) to CardsHeaderType.PACK_ENCOUNTER_SET
            }
            else showPack to CardsHeaderType.PACK
        }
        "name" -> {
            (previousItem == null) to null
        }
        "position" -> {
            (if (nextItem.reprintPackCode != null)
                    previousItem?.reprintPackCode != nextItem.reprintPackCode
                else previousItem?.packCode != nextItem.packCode
            ) to CardsHeaderType.POSITION
        }
        "encounter" -> {
            (previousItem?.encounterCode != nextItem.encounterCode) to CardsHeaderType.ENCOUNTER_SET
        }
        else -> false to null
    }
}