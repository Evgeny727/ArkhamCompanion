package com.arkhamcompanion.domain.arkhamql.fields

import com.arkhamcompanion.domain.arkhamql.ast.QueryField
import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldType

object QueryFields {

    val agility = QueryField(
        name = "agility",
        type = QueryFieldType.NUMBER,
        aliases = setOf("ag", "foot", "a"),
    )

    val bonded = QueryField(
        name = "bonded",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("bo"),
    )

    val chapter = QueryField(
        name = "chapter",
        type = QueryFieldType.NUMBER,
        aliases = setOf("ch"),
    )

    val clues = QueryField(
        name = "clues",
        type = QueryFieldType.NUMBER,
        aliases = setOf("cl"),
    )

    val combat = QueryField(
        name = "combat",
        type = QueryFieldType.NUMBER,
        aliases = setOf("cb", "fist", "c"),
    )

    val cost = QueryField(
        name = "cost",
        type = QueryFieldType.NUMBER,
        aliases = setOf("co", "o"),
    )

    val customizable = QueryField(
        name = "customizable",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("cus"),
    )

    val cycle = QueryField(
        name = "cycle",
        type = QueryFieldType.STRING,
        aliases = setOf("cy", "y"),
    )

    val damage = QueryField(
        name = "damage",
        type = QueryFieldType.NUMBER,
        aliases = setOf("dmg"),
    )

    val deckLimit = QueryField(
        name = "deck_limit",
        type = QueryFieldType.NUMBER,
        aliases = setOf("dl", "limit"),
    )

    val doom = QueryField(
        name = "doom",
        type = QueryFieldType.NUMBER,
        aliases = setOf("do"),
    )

    val encounterSet = QueryField(
        name = "encounter_set",
        type = QueryFieldType.STRING,
        aliases = setOf("en", "encounter", "set"),
    )

    val evade = QueryField(
        name = "evade",
        type = QueryFieldType.NUMBER,
        aliases = setOf("ev"),
    )

    val exceptional = QueryField(
        name = "exceptional",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("ex"),
    )

    val exile = QueryField(
        name = "exile",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("exl"),
    )

    val faction = QueryField(
        name = "faction",
        type = QueryFieldType.STRING,
        aliases = setOf("cls", "class", "f"),
    )

    val fight = QueryField(
        name = "fight",
        type = QueryFieldType.NUMBER,
        aliases = setOf("fi"),
    )

    val flavor = QueryField(
        name = "flavor",
        type = QueryFieldType.STRING,
        aliases = setOf("fl", "v"),
    )

    //TODO: not sure if possible to implement this
    val hasUpgrade = QueryField(
        name = "has_upgrade",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("hu"),
    )

    val healsDamage = QueryField(
        name = "heals_damage",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("hd"),
    )

    val healsHorror = QueryField(
        name = "heals_horror",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("hh"),
    )

    val health = QueryField(
        name = "health",
        type = QueryFieldType.NUMBER,
        aliases = setOf("hp", "h"),
    )

    val horror = QueryField(
        name = "horror",
        type = QueryFieldType.NUMBER,
        aliases = setOf("ho"),
    )

    val id = QueryField(
        name = "id",
        type = QueryFieldType.STRING,
        aliases = setOf("code"),
    )

    val illustrator = QueryField(
        name = "illustrator",
        type = QueryFieldType.STRING,
        aliases = setOf("il", "illu", "l"),
    )

    val intellect = QueryField(
        name = "intellect",
        type = QueryFieldType.NUMBER,
        aliases = setOf("in", "int", "book", "i"),
    )

    //TODO: not sure if possible to implement this
    val investigatorAccess = QueryField(
        name = "investigator_access",
        type = QueryFieldType.STRING,
        aliases = setOf("ia", "do"),
    )

    //TODO: not sure if possible to implement this
    val inDeck = QueryField(
        name = "in_deck",
        type = QueryFieldType.NUMBER,
    )

    //TODO: not sure if possible to implement this
    val inSideDeck = QueryField(
        name = "in_side_deck",
        type = QueryFieldType.NUMBER,
    )

    val isFavorite = QueryField(
        name = "is_favorite",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("fav"),
    )

    val isUpgrade = QueryField(
        name = "is_upgrade",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("iu"),
    )

    val level = QueryField(
        name = "level",
        type = QueryFieldType.NUMBER,
        aliases = setOf("lvl", "p"),
    )

    val multiclass = QueryField(
        name = "multiclass",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("mu", "multi"),
    )

    val myriad = QueryField(
        name = "myriad",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("my"),
    )

    val name = QueryField(
        name = "name",
        type = QueryFieldType.STRING,
        aliases = setOf("na"),
    )

    val parallel = QueryField(
        name = "parallel",
        type = QueryFieldType.BOOLEAN,
    )

    val pack = QueryField(
        name = "pack",
        type = QueryFieldType.STRING,
        aliases = setOf("pa", "e"),
    )

    val permanent = QueryField(
        name = "permanent",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("pe", "perm"),
    )

    val quantity = QueryField(
        name = "quantity",
        type = QueryFieldType.NUMBER,
        aliases = setOf("qt", "qty"),
    )

    val reverseType = QueryField(
        name = "reverse_type",
        type = QueryFieldType.STRING,
        aliases = setOf("rt"),
    )

    val sanity = QueryField(
        name = "sanity",
        type = QueryFieldType.NUMBER,
        aliases = setOf("sa", "s"),
    )

    val shroud = QueryField(
        name = "shroud",
        type = QueryFieldType.NUMBER,
        aliases = setOf("sh"),
    )

    val slot = QueryField(
        name = "slot",
        type = QueryFieldType.STRING,
        aliases = setOf("sl", "z"),
    )

    val specialist = QueryField(
        name = "specialist",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("sp"),
    )

    val stage = QueryField(
        name = "stage",
        type = QueryFieldType.NUMBER,
        aliases = setOf("sg"),
    )

    val subname = QueryField(
        name = "subname",
        type = QueryFieldType.STRING,
        aliases = setOf("sn"),
    )

    val subtype = QueryField(
        name = "subtype",
        type = QueryFieldType.STRING,
        aliases = setOf("st", "b"),
    )

    val tabooSet = QueryField(
        name = "taboo_set",
        type = QueryFieldType.STRING,
        aliases = setOf("ts"),
    )

    val text = QueryField(
        name = "text",
        type = QueryFieldType.STRING,
        aliases = setOf("txt", "x"),
    )

    val trait = QueryField(
        name = "trait",
        type = QueryFieldType.STRING,
        aliases = setOf("tr", "k"),
    )

    val type = QueryField(
        name = "type",
        type = QueryFieldType.STRING,
        aliases = setOf("ty", "t"),
    )

    val unique = QueryField(
        name = "unique",
        type = QueryFieldType.BOOLEAN,
        aliases = setOf("un", "u"),
    )

    val vengeance = QueryField(
        name = "vengeance",
        type = QueryFieldType.NUMBER,
        aliases = setOf("ve"),
    )

    val victory = QueryField(
        name = "victory",
        type = QueryFieldType.NUMBER,
        aliases = setOf("vp", "j"),
    )

    val wild = QueryField(
        name = "wild",
        type = QueryFieldType.NUMBER,
        aliases = setOf("wd", "d"),
    )

    val willpower = QueryField(
        name = "willpower",
        type = QueryFieldType.NUMBER,
        aliases = setOf("wp", "will", "brain", "w"),
    )

    val xp = QueryField(
        name = "xp",
        type = QueryFieldType.NUMBER,
    )

    val all = listOf(
        agility,
        bonded,
        chapter,
        clues,
        combat,
        cost,
        customizable,
        cycle,
        damage,
        deckLimit,
        doom,
        encounterSet,
        evade,
        exceptional,
        exile,
        faction,
        fight,
        flavor,
        hasUpgrade,
        healsDamage,
        healsHorror,
        health,
        horror,
        id,
        illustrator,
        intellect,
        investigatorAccess,
        inDeck,
        inSideDeck,
        isFavorite,
        isUpgrade,
        level,
        multiclass,
        myriad,
        name,
        parallel,
        pack,
        permanent,
        quantity,
        reverseType,
        sanity,
        shroud,
        slot,
        specialist,
        stage,
        subname,
        subtype,
        tabooSet,
        text,
        trait,
        type,
        unique,
        vengeance,
        victory,
        wild,
        willpower,
        xp,
    )
}