package com.arkhamcompanion.data.local.arkhamql

import com.arkhamcompanion.data.local.cards.CardSearchResultEntity
import com.arkhamcompanion.data.objects.CardCache
import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldReference
import com.arkhamcompanion.domain.arkhamql.ast.QueryValue
import com.arkhamcompanion.domain.arkhamql.evaluator.EvaluatedValues
import com.arkhamcompanion.domain.arkhamql.evaluator.QueryEvaluationException
import com.arkhamcompanion.domain.arkhamql.evaluator.QueryFieldResolver
import com.arkhamcompanion.domain.arkhamql.fields.QueryFields
import kotlinx.serialization.json.jsonObject

class QueryFieldResolverImpl : QueryFieldResolver<CardSearchResultEntity> {

    override fun resolve(
        reference: QueryFieldReference,
        card: CardSearchResultEntity,
        matchBacks: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        return when (reference.field) {
            QueryFields.agility -> {
                val frontValue = (card.front.skills.skillAgility ?: 0).toQueryValue()
                val backValue = (card.back?.skills?.skillAgility ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.bonded -> {
                val isBonded = !CardCache.bonded[card.front.code].isNullOrEmpty()

                EvaluatedValues.Single(isBonded.toQueryValue())
            }

            QueryFields.chapter -> {
                EvaluatedValues.Single(card.front.chapter.toQueryValue())
            }

            QueryFields.clues -> {
                val frontValue = card.front.clues.toQueryValue()
                val backValue = card.back?.clues.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.combat -> {
                val frontValue = (card.front.skills.skillCombat ?: 0).toQueryValue()
                val backValue = (card.back?.skills?.skillCombat ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.cost -> {
                val frontValue = card.front.cost.toQueryValue()
                val backValue =card.back?.cost.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.customizable -> {
                val customizable = card.front.realCustomizationText != null

                EvaluatedValues.Single(customizable.toQueryValue())
            }

            QueryFields.cycle -> resolveCycle(card, reference.real, matchReal)

            QueryFields.damage -> {
                val frontValue = (card.front.enemyDamage ?: 0).toQueryValue()
                val backValue = (card.back?.enemyDamage ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.deckLimit -> {
                val frontValue = card.front.deckLimit.toQueryValue()
                val backValue = card.back?.deckLimit.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.doom -> {
                val frontValue = card.front.doom.toQueryValue()
                val backValue = card.back?.doom.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.encounterSet -> resolveEncounterSet(card, reference.real, matchReal)

            QueryFields.evade -> {
                val frontValue = card.front.enemyEvade.toQueryValue()
                val backValue = card.back?.enemyEvade.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.exceptional -> {
                EvaluatedValues.Single(card.front.exceptional.toQueryValue())
            }

            QueryFields.exile -> {
                EvaluatedValues.Single(card.front.exile.toQueryValue())
            }

            QueryFields.faction -> resolveFaction(
                card,
                reference.back,
                matchBacks,
                reference.real,
                matchReal
            )

            QueryFields.fight -> {
                val frontValue = card.front.enemyFight.toQueryValue()
                val backValue = card.back?.enemyFight.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.flavor -> {
                val value = card.front.translation.flavor.toQueryValue()
                val backValue = (card.front.translation.backFlavor
                    ?: card.back?.translation?.flavor).toQueryValue()
                val realValue = card.front.realFlavor.toQueryValue()
                val realBackValue = (card.front.realBackFlavor
                    ?: card.back?.realFlavor).toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.healsDamage -> {
                val healsDamage = CardCache.tags["hd"]?.contains(card.front.code) == true

                EvaluatedValues.Single(healsDamage.toQueryValue())
            }

            QueryFields.healsHorror -> {
                val healsHorror = CardCache.tags["hh"]?.contains(card.front.code) == true

                EvaluatedValues.Single(healsHorror.toQueryValue())
            }

            QueryFields.health -> {
                val frontValue = card.front.health.toQueryValue()
                val backValue = card.back?.health.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.horror -> {
                val frontValue = (card.front.enemyHorror ?: 0).toQueryValue()
                val backValue = (card.back?.enemyHorror ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.id -> {
                val frontValue = card.front.id.toQueryValue()
                val backValue = card.back?.id.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.illustrator -> {
                val value = card.front.illustrator.toQueryValue()
                val backValue = (card.front.backIllustrator
                    ?: card.back?.illustrator).toQueryValue()

                resolveValueWithBack(
                    value,
                    backValue,
                    reference.back,
                    matchBacks,
                )
            }

            QueryFields.intellect -> {
                val frontValue = (card.front.skills.skillIntellect ?: 0).toQueryValue()
                val backValue = (card.back?.skills?.skillIntellect ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.isUpgrade -> {
                val isUpgrade = !CardCache.level[card.front.code].isNullOrEmpty()
                        && card.front.xp != 0

                EvaluatedValues.Single(isUpgrade.toQueryValue())
            }

            QueryFields.level -> {
                val frontValue = card.front.xp.toQueryValue()
                val backValue = card.back?.xp.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.multiclass -> {
                val multiclass = card.front.faction2Code != null || card.front.faction3Code != null

                EvaluatedValues.Single(multiclass.toQueryValue())
            }

            QueryFields.myriad -> {
                EvaluatedValues.Single(card.front.myriad.toQueryValue())
            }

            QueryFields.name -> {
                val value = card.front.translation.name.toQueryValue()
                val backValue = (card.front.translation.backName
                    ?: card.back?.translation?.name).toQueryValue()
                val realValue = card.front.realName.toQueryValue()
                val realBackValue = (card.front.realBackName
                    ?: card.back?.realName).toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.parallel -> {
                EvaluatedValues.Single(card.front.parallel.toQueryValue())
            }

            QueryFields.pack -> resolvePack(card, reference.real, matchReal)

            QueryFields.permanent -> {
                val frontValue = card.front.permanent.toQueryValue()
                val backValue = (card.back?.permanent ?: false).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.quantity -> {
                val frontValue = card.front.quantity.toQueryValue()
                val backValue = (card.back?.quantity ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.reverseType -> {
                EvaluatedValues.Single(card.front.backType.toQueryValue())
            }

            QueryFields.sanity -> {
                val frontValue = card.front.sanity.toQueryValue()
                val backValue = card.back?.sanity.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.shroud -> {
                val frontValue = card.front.shroud.toQueryValue()
                val backValue = card.back?.shroud.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.slot -> {
                val value = card.front.translation.slot.splitMultiValue().toQueryValue()
                val realValue = card.front.realSlot.splitMultiValue().toQueryValue()

                resolveValuesWithReal(
                    value,
                    realValue,
                    reference.real,
                    matchReal
                )
            }

            QueryFields.specialist -> {
                val isSpecialist = card.front.restrictions?.jsonObject?.contains("trait") == true

                EvaluatedValues.Single(isSpecialist.toQueryValue())
            }

            QueryFields.stage -> {
                val frontValue = card.front.stage.toQueryValue()
                val backValue = card.back?.stage.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.subname -> {
                val value = card.front.translation.subname.toQueryValue()
                val backValue = (card.front.translation.backSubname
                    ?: card.back?.translation?.subname).toQueryValue()
                val realValue = card.front.realSubname.toQueryValue()
                val realBackValue = (card.front.realBackSubname
                    ?: card.back?.realSubname).toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.subtype -> {
                val value = card.front.subTypeName.toQueryValue()
                val backValue = card.back?.subTypeName.toQueryValue()
                val realValue = card.front.subTypeCode.toQueryValue()
                val realBackValue = card.back?.subTypeCode.toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.tabooSet -> {
                val tabooName = if (card.front.tabooPlaceholder) null else card.front.tabooSetName

                EvaluatedValues.Single(tabooName.toQueryValue())
            }

            QueryFields.text -> {
                val value = card.front.translation.text.toQueryValue()
                val backValue = (card.front.translation.backText
                    ?: card.back?.translation?.text).toQueryValue()
                val realValue = card.front.realText.toQueryValue()
                val realBackValue = (card.front.realBackText
                    ?: card.back?.realText).toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.trait -> {
                resolveTraits(
                    card,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.type -> {
                val value = card.front.typeName.toQueryValue()
                val backValue = card.back?.typeName.toQueryValue()
                val realValue = card.front.typeCode.toQueryValue()
                val realBackValue = card.back?.typeCode.toQueryValue()

                resolveTextField(
                    value,
                    backValue,
                    realValue,
                    realBackValue,
                    reference.back,
                    reference.real,
                    matchBacks,
                    matchReal
                )
            }

            QueryFields.unique -> {
                val frontValue = card.front.isUnique.toQueryValue()
                val backValue = (card.back?.isUnique ?: false).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.vengeance -> {
                val frontValue = card.front.vengeance.toQueryValue()
                val backValue = card.back?.vengeance.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.victory -> {
                val frontValue = card.front.victory.toQueryValue()
                val backValue = card.back?.victory.toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.wild -> {
                val frontValue = (card.front.skills.skillWild ?: 0).toQueryValue()
                val backValue = (card.back?.skills?.skillWild ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.willpower -> {
                val frontValue = (card.front.skills.skillWillpower ?: 0).toQueryValue()
                val backValue = (card.back?.skills?.skillWillpower ?: 0).toQueryValue()

                resolveValueWithBack(
                    frontValue,
                    backValue,
                    reference.back,
                    matchBacks
                )
            }

            QueryFields.xp -> {
                resolveXp(
                    card,
                    reference.back,
                    matchBacks
                )
            }

            else -> throw QueryEvaluationException(
                "Unknown field: ${reference.field.name}",
            )
        }
    }

    private fun resolveValueWithBack(
        value: QueryValue,
        backValue: QueryValue,
        onlyBack: Boolean,
        matchBacks: Boolean,
    ): EvaluatedValues {

        if (onlyBack) return EvaluatedValues.Single(backValue)

        if (matchBacks) return EvaluatedValues.Multiple(listOf(value, backValue))

        return EvaluatedValues.Single(value)
    }

    private fun resolveValuesWithReal(
        values: List<QueryValue>,
        realValues: List<QueryValue>,
        onlyReal: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {

        if (onlyReal) return EvaluatedValues.Multiple(realValues)

        if (matchReal) return EvaluatedValues.Multiple(values + realValues)

        return EvaluatedValues.Multiple(values)
    }

    private fun resolveCycle(
        card: CardSearchResultEntity,
        onlyReal: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        val cycleName = card.front.cycleName.toQueryValue()
        val cycleRealName = card.front.cycleRealName.toQueryValue()

        val values = listOf(card.front.cycleCode.toQueryValue())

        if (onlyReal) return EvaluatedValues.Multiple(values + cycleRealName)

        if (matchReal) return EvaluatedValues.Multiple(values + cycleRealName + cycleName)

        return EvaluatedValues.Multiple(values + cycleName)
    }

    private fun resolveEncounterSet(
        card: CardSearchResultEntity,
        onlyReal: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        val encounterSetName = card.front.encounterName.toQueryValue()
        val encounterSetRealName = card.front.encounterRealName.toQueryValue()

        val values = listOf(card.front.encounterCode.toQueryValue())

        if (onlyReal) return EvaluatedValues.Multiple(values + encounterSetRealName)

        if (matchReal) return EvaluatedValues.Multiple(values + encounterSetRealName + encounterSetName)

        return EvaluatedValues.Multiple(values + encounterSetName)
    }

    private fun resolveFaction(
        card: CardSearchResultEntity,
        onlyBack: Boolean,
        matchBacks: Boolean,
        onlyReal: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        val frontFactions = listOfNotNull(
            card.front.factionName.toQueryValue(), card.front.faction2Name?.toQueryValue(),
            card.front.faction3Name?.toQueryValue(),
        )
        val backFaction = card.back?.factionName.toQueryValue()
        val frontRealFactions = listOfNotNull(
            card.front.factionCode.toQueryValue(), card.front.faction2Code?.toQueryValue(),
            card.front.faction3Code?.toQueryValue(),
        )
        val backRealFaction = card.back?.factionCode.toQueryValue()

        val values = buildSet {
            if (matchBacks && !onlyBack) {
                if (matchReal && !onlyReal) {
                    addAll(frontFactions)
                    add(backFaction)
                    addAll(frontRealFactions)
                    add(backRealFaction)
                } else if (onlyReal) {
                    addAll(frontRealFactions)
                    add(backRealFaction)
                } else {
                    addAll(frontFactions)
                    add(backFaction)
                }
            } else if (onlyBack) {
                if (matchReal && !onlyReal) {
                    add(backFaction)
                    add(backRealFaction)
                } else if (onlyReal) {
                    add(backRealFaction)
                } else {
                    add(backFaction)
                }
            } else {
                if (matchReal && !onlyReal) {
                    addAll(frontFactions)
                    addAll(frontRealFactions)
                } else if (onlyReal) {
                    addAll(frontRealFactions)
                } else {
                    addAll(frontFactions)
                }
            }
        }

        return EvaluatedValues.Multiple(values.toList())
    }

    private fun resolveTextField(
        string: QueryValue,
        backString: QueryValue,
        realString: QueryValue,
        backRealString: QueryValue,
        onlyBack: Boolean,
        onlyReal: Boolean,
        matchBacks: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {

        val values = buildList {
            if (matchBacks && !onlyBack) {
                if (matchReal && !onlyReal) {
                    add(string)
                    add(backString)
                    add(realString)
                    add(backRealString)
                } else if (onlyReal) {
                    add(realString)
                    add(backRealString)
                } else {
                    add(string)
                    add(backString)
                }
            } else if (onlyBack) {
                if (matchReal && !onlyReal) {
                    add(backString)
                    add(backRealString)
                } else if (onlyReal) {
                    add(backRealString)
                } else {
                    add(backString)
                }
            } else {
                if (matchReal && !onlyReal) {
                    add(string)
                    add(realString)
                } else if (onlyReal) {
                    add(realString)
                } else {
                    add(string)
                }
            }
        }

        return EvaluatedValues.Multiple(values)
    }

    private fun resolvePack(
        card: CardSearchResultEntity,
        onlyReal: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        val packName = card.front.packName.toQueryValue()
        val packRealName = card.front.packRealName.toQueryValue()

        val values = listOf(card.front.packCode.toQueryValue())

        if (onlyReal) return EvaluatedValues.Multiple(values + packRealName)

        if (matchReal) return EvaluatedValues.Multiple(values + packRealName + packName)

        return EvaluatedValues.Multiple(values + packName)
    }

    private fun resolveTraits(
        card: CardSearchResultEntity,
        onlyBack: Boolean,
        onlyReal: Boolean,
        matchBacks: Boolean,
        matchReal: Boolean,
    ): EvaluatedValues {
        val value = card.front.translation.traits.splitMultiValue().toQueryValue()
        val backValue = (card.front.translation.backTraits.splitMultiValue()
            ?: card.back?.translation?.traits.splitMultiValue()).toQueryValue()
        val realValue = card.front.realTraits.splitMultiValue().toQueryValue()
        val realBackValue = (card.front.realBackTraits.splitMultiValue()
            ?: card.back?.realTraits.splitMultiValue()).toQueryValue()


        val values = buildSet {
            if (matchBacks && !onlyBack) {
                if (matchReal && !onlyReal) {
                    addAll(value)
                    addAll(backValue)
                    addAll(realValue)
                    addAll(realBackValue)
                } else if (onlyReal) {
                    addAll(realValue)
                    addAll(realBackValue)
                } else {
                    addAll(value)
                    addAll(backValue)
                }
            } else if (onlyBack) {
                if (matchReal && !onlyReal) {
                    addAll(backValue)
                    addAll(realBackValue)
                } else if (onlyReal) {
                    addAll(realBackValue)
                } else {
                    addAll(backValue)
                }
            } else {
                if (matchReal && !onlyReal) {
                    addAll(value)
                    addAll(realValue)
                } else if (onlyReal) {
                    addAll(realValue)
                } else {
                    addAll(value)
                }
            }
        }

        return EvaluatedValues.Multiple(values.toList())
    }

    private fun resolveXp(
        card: CardSearchResultEntity,
        onlyBack: Boolean,
        matchBacks: Boolean,
    ): EvaluatedValues {
        var totalXp = card.front.xp
        if (card.front.exceptional && totalXp != null) totalXp *= 2
        if (card.front.tabooXp != null  && totalXp != null) totalXp += card.front.tabooXp

        return when {
            matchBacks && !onlyBack -> {
                EvaluatedValues.Multiple(listOf(
                    totalXp.toQueryValue(),
                    card.back?.xp.toQueryValue()
                ))
            }

            onlyBack -> {
                EvaluatedValues.Single(card.back?.xp.toQueryValue())
            }

            else -> {
                EvaluatedValues.Single(totalXp.toQueryValue())
            }
        }
    }

    private fun String?.splitMultiValue(): List<String>? {
        if (this == null) return null

        return this.split(".").map { it.trim() }
    }

    private fun Int?.toQueryValue() =
        if (this == null) QueryValue.Null else QueryValue.Number(this)

    private fun Boolean?.toQueryValue() =
        if (this == null) QueryValue.Null else QueryValue.Boolean(this)

    private fun String?.toQueryValue() =
        if (this == null) QueryValue.Null else QueryValue.String(this)

    private fun List<String>?.toQueryValue(): List<QueryValue> {
        return this?.map { it.toQueryValue() } ?: listOf(QueryValue.Null)
    }

}