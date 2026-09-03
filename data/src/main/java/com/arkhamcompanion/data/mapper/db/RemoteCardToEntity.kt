package com.arkhamcompanion.data.mapper.db

import com.arkhamcompanion.GetTranslationDataQuery
import com.arkhamcompanion.data.local.cards.CardEntity
import com.arkhamcompanion.data.local.cards.CardSubtypeEntity
import com.arkhamcompanion.data.local.cards.CardTypeEntity
import com.arkhamcompanion.data.local.cards.Skills
import com.arkhamcompanion.data.local.cards.Translation
import com.arkhamcompanion.data.local.cards.patches.CardPatch
import com.arkhamcompanion.data.local.cards.patches.resolve
import com.arkhamcompanion.data.local.meta.CycleEntity
import com.arkhamcompanion.data.local.meta.PackEntity
import com.arkhamcompanion.data.objects.CardSortOrder.sortByFactionOrder
import com.arkhamcompanion.data.objects.CardSortOrder.sortBySlotOrder
import com.arkhamcompanion.data.objects.CardSortOrder.sortByTypeOrder
import com.arkhamcompanion.domain.objects.normalizeForSearch
import com.arkhamcompanion.domain.model.cards.ARKHAM_BUILD_BASE_IMAGE_URL
import com.arkhamcompanion.fragment.CoreCardText
import com.arkhamcompanion.fragment.SingleCard

val REPRINT_PACKS = setOf("dwl", "ptc", "tfa", "tcu", "tde", "tic")

/**
 * Extension function to convert [SingleCard] with [CoreCardText] to [CardEntity]
 */
fun SingleCard.toEntity(
    coreCardText: CoreCardText?,
    cardPatch: CardPatch,
    cycle: CycleEntity,
    pack: PackEntity,
    locale: String
): CardEntity {
    val translation = coreCardText.toTranslation(this)
    val patchValues = cardPatch.values

    val patchedBackType = patchValues.backType.resolve()
    val backType = when {
        double_sided == true || back_link_id != null -> "card"
        patchedBackType != null -> patchedBackType
        faction_code == "mythos" || (encounter_code != null && deck_limit == null) -> "encounter"
        else -> "player"
    }

    val reprintPackCode = if (cycle.code in REPRINT_PACKS) {
        cycle.code + if (encounter_code == null) "p" else "c"
    } else null

    return CardEntity(
        id = id,
        code = code,
        advancedFor = advanced_for,
        altArtInvestigator = alt_art_investigator ?: false,
        alternateOfCode = alternate_of_code,
        alternateRequiredCode = alternate_required_code,
        backLinkId = patchValues.backLinkId.resolve(back_link_id),
        backIllustrator = back_illustrator,
        backType = backType,
        clues = clues,
        cluesFixed = clues_fixed ?: false,
        cost = cost,
        customizationOptions = customization_options,
        cycleCode = cycle.code,
        deckLimit = patchValues.deckLimit.resolve(deck_limit),
        deckOptions = deck_options,
        deckRequirements = simple_deck_requirements,
        doom = doom,
        doomPerInvestigator = patchValues.doomPerInvestigator.resolve() ?: false,
        doubleSided = double_sided ?: false,
        duplicateOfCode = patchValues.duplicateOf.resolve(duplicate_of_code),
        encounterCode = encounter_code,
        encounterPosition = encounter_position,
        enemyDamage = enemy_damage,
        enemyHorror = enemy_horror,
        enemyFight = enemy_fight,
        enemyFightPerInvestigator = patchValues.enemyFightPerInvestigator.resolve() ?: false,
        enemyEvade = enemy_evade,
        enemyEvadePerInvestigator = patchValues.enemyEvadePerInvestigator.resolve() ?: false,
        errataDate = errata_date,
        exceptional = exceptional ?: false,
        exile = exile ?: false,
        factionCode = faction_code,
        faction2Code = faction2_code,
        faction3Code = faction3_code,
        gender = gender?.rawValue,
        gameBeginAttribute = patchValues.gameBeginAttribute.resolve(),
        health = health,
        healthPerInvestigator = health_per_investigator ?: false,
        hidden = patchValues.hidden.resolve(
            //Make alt art investigators not hidden (except for Hank)
            if (type_code.rawValue == "investigator" && code != "10016a" && code != "10016b") false else hidden
        ) ?: false,
        illustrator = illustrator,
        investigatorId = investigator_id,
        isUnique = is_unique ?: false,
        myriad = myriad ?: false,
        official = official,
        packCode = pack_code,
        packPosition = pack_position,
        parallel = pack.cycleCode == "parallel",
        parallelOfCode = parallel_of_code,
        permanent = permanent ?: false,
        position = position,
        preview = patchValues.preview.resolve(preview) ?: false,
        reprintPackCode = reprintPackCode,
        realBackFlavor = real_back_flavor,
        realBackName = real_back_name,
        realBackSubname = real_back_subname,
        realBackText = real_back_text,
        realBackTraits = real_back_traits,
        realCustomizationChange = real_customization_change,
        realCustomizationText = real_customization_text,
        realEncounterSetName = real_encounter_set_name,
        realFlavor = real_flavor,
        realName = real_name,
        realPackName = real_pack_name,
        realSlot = real_slot?.ifBlank { null },
        realSubname = real_subname,
        realTabooOriginalBackText = real_taboo_original_back_text,
        realTabooOriginalText = real_taboo_original_text,
        realTabooTextChange = real_taboo_text_change,
        realText = real_text,
        realTraits = real_traits,
        replacementFor = replacement_for,
        reprintOfCode = patchValues.reprintOf.resolve(reprint_of_code),
        restrictions = restrictions,
        sanity = sanity,
        shroud = patchValues.shroud.resolve(shroud),
        shroudPerInvestigator = patchValues.shroudPerInvestigator.resolve() ?: false,
        sideDeckOptions = side_deck_options,
        sideDeckRequirements = simple_side_deck_requirements,
        signatureFor = signature_for,
        skills = Skills(
            skillWillpower = skill_willpower,
            skillIntellect = patchValues.skillIntellect.resolve(skill_intellect),
            skillCombat = skill_combat,
            skillAgility = skill_agility,
            skillWild = skill_wild
        ),
        spoiler = spoiler ?: false,
        stage = stage,
        subTypeCode = when(code) {
            "zcxc_00264" -> "weakness" //Fix subtype for fanmade card
            else -> subtype_code
        },
        tags = patchValues.tags.resolve(tags),
        xp = xp,
        vengeance = vengeance,
        victory = victory,
        quantity = quantity,
        typeCode = type_code.rawValue,
        //Exclude thumbnail for random basic weakness
        thumbnailurl = if (official && id != "01000") ARKHAM_BUILD_BASE_IMAGE_URL + "thumbnails/${code}.webp" else null,
        backthumbnailurl = if (official && double_sided == true && back_link_id == null) {
            ARKHAM_BUILD_BASE_IMAGE_URL + "thumbnails/${code}b.webp"
        } else null,
        imageurl = when {
            official -> {
                ARKHAM_BUILD_BASE_IMAGE_URL + "optimized/${
                    if (taboo_set_id != 0 && taboo_placeholder != true) id else code
                }.webp"
            }
            else -> imageurl
        },
        backimageurl = when {
            backType != "card" -> ARKHAM_BUILD_BASE_IMAGE_URL + "back_${backType}.jpg"
            official && double_sided == true && back_link_id == null -> {
                ARKHAM_BUILD_BASE_IMAGE_URL + "optimized/${code}b${
                    if ((taboo_set_id ?: 0) != 0 && taboo_placeholder != true) "-$taboo_set_id" else ""
                }.webp"
            }
            official && back_link_id != null -> {
                ARKHAM_BUILD_BASE_IMAGE_URL + "optimized/${back_link_id}.webp"
            }
            else -> backimageurl
        },
        tabooXp = taboo_xp,
        tabooSetId = when(taboo_set_id) {
            0 -> null //Fix taboo set id for original cards which are/were in any taboo set
            else -> taboo_set_id
        },
        tabooPlaceholder = if (taboo_set_id == 0) true else taboo_placeholder ?: false,
        translation = translation,
        sortByType = sortByTypeOrder(type_code.rawValue, subtype_code),
        sortByFaction = sortByFactionOrder(faction_code, faction2_code),
        sortByPack = cycle.position * 200 + pack.position,
        sortByCycle = cycle.position,
        sortBySlot = sortBySlotOrder(real_slot, permanent, type_code.rawValue),
        searchName = listOfNotNull(translation.name, translation.subname)
            .joinToString(" ")
            .normalizeForSearch(),
        searchNameBack = listOfNotNull(translation.backName, translation.backSubname)
            .joinToString(" ")
            .normalizeForSearch(),
        searchGame = listOfNotNull(translation.text, translation.traits, translation.customizationText)
                .joinToString(" ")
                .normalizeForSearch(),
        searchGameBack = listOfNotNull(translation.backText, translation.backTraits)
            .joinToString(" ")
            .normalizeForSearch(),
        searchFlavor = translation.flavor?.normalizeForSearch() ?: "",
        searchFlavorBack = translation.backFlavor?.normalizeForSearch() ?: "",
        searchRealName = if (locale == "en") null else
            listOfNotNull(real_name, real_subname)
                .joinToString(" ")
                .normalizeForSearch(),
        searchRealNameBack = if (locale == "en") null else
            listOfNotNull(real_back_name, real_back_subname)
                .joinToString(" ")
                .normalizeForSearch(),
        searchRealGame = if (locale == "en") null else
            listOfNotNull(real_text, real_traits, real_customization_text)
                .joinToString(" ")
                .normalizeForSearch(),
        searchRealGameBack = if (locale == "en") null else
            listOfNotNull(real_back_text, real_back_traits)
                .joinToString(" ")
                .normalizeForSearch(),
        searchRealFlavor = if (locale == "en") null else
            listOfNotNull(real_flavor)
                .joinToString(" ")
                .normalizeForSearch(),
        searchRealFlavorBack = if (locale == "en") null else
            listOfNotNull(real_back_flavor)
                .joinToString(" ")
                .normalizeForSearch(),
    )
}

/**
 * Extension function to convert [CoreCardText] to [Translation]
 */
fun CoreCardText?.toTranslation(card: SingleCard): Translation {
    val backTraits = this?.back_traits
        ?: if (card.double_sided == true &&
            (card.type_code.rawValue == "location" || card.type_code.rawValue == "enemy_location")
            ) {
                this?.traits ?: card.real_traits
        } else card.real_back_traits

    return Translation(
        backFlavor = (this?.back_flavor ?: card.real_back_flavor)?.preprocessCardText(processBullets = false),
        backName = this?.back_name ?: card.real_back_name,
        backSubname = this?.back_subname ?: card.real_back_subname,
        backText = (this?.back_text ?: card.real_back_text)?.preprocessCardText(processBullets = true),
        backTraits = backTraits,
        customizationChange = this?.customization_change ?: card.real_customization_change,
        customizationText = this?.customization_text ?: card.real_customization_text,
        flavor = (this?.flavor ?: card.real_flavor)?.preprocessCardText(processBullets = false),
        name = this?.name ?: card.real_name,
        slot = (this?.slot ?: card.real_slot)?.ifBlank { null },
        subname = this?.subname ?: card.real_subname,
        tabooOriginalBackText = (this?.taboo_original_back_text ?: card.real_taboo_original_back_text)
            ?.preprocessCardText(processBullets = true),
        tabooOriginalText = (this?.taboo_original_text ?: card.real_taboo_original_text)
            ?.preprocessCardText(processBullets = true),
        tabooTextChange = this?.taboo_text_change ?: card.real_taboo_text_change,
        text = (this?.text ?: card.real_text)?.preprocessCardText(processBullets = true),
        traits = this?.traits ?: card.real_traits,
    )
}

/**
 * Extension function to convert [GetTranslationDataQuery.Card_type_name] to [CardTypeEntity]
 */
fun GetTranslationDataQuery.Card_type_name.toEntity(): CardTypeEntity {
    return CardTypeEntity(
        code = code.rawValue,
        name = name
    )
}

/**
 * Extension function to convert [GetTranslationDataQuery.Card_subtype_name] to [CardSubtypeEntity]
 */
fun GetTranslationDataQuery.Card_subtype_name.toEntity(): CardSubtypeEntity {
    return CardSubtypeEntity(
        code = code,
        name = name
    )
}

private val WEIRD_BULLET_REGEX = Regex("""\\u2022""")
private val LINEBREAK_REGEX = Regex("""(\/n|<br\/?>)""")
private val INDENTED_BULLET_REGEX = Regex("""(^\s?--|^-—\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val BULLET_REGEX = Regex("""(^\s?-|^—\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val GUIDE_BULLET_REGEX = Regex("""(^\s?=|^=\s+)([^0-9].+)$""", RegexOption.MULTILINE)
private val PARAGRAPH_BULLET_REGEX = Regex("""(<p>- )|(<p>–)""", RegexOption.MULTILINE)
private val DOUBLE_BRACKET_REGEX = Regex("""\[\[([^\]]+)\]\]""")

internal fun String.preprocessCardText(processBullets: Boolean): String {
    val result = this
        .replace(WEIRD_BULLET_REGEX, "•")
        .replace(LINEBREAK_REGEX, "\n")
        .replace(DOUBLE_BRACKET_REGEX) { match ->
            "<trait>${match.groupValues[1]}</trait>"
        }

    if (processBullets) {
        return result
            .replace(INDENTED_BULLET_REGEX) { match ->
                "\t[bullet] ${match.groupValues[2]}"
            }
            .replace(BULLET_REGEX) { match ->
                "[bullet] ${match.groupValues[2]}"
            }
            .replace(GUIDE_BULLET_REGEX) { match ->
                "[guide_bullet] ${match.groupValues[2]}"
            }
            .replace(PARAGRAPH_BULLET_REGEX, "<p>[bullet]")
    }

    return result
}