package com.arkhamcompanion.ui.utils

import com.arkhamcompanion.R

fun getLocalizedAction(code: String) =
    when (code) {
        "fight" -> R.string.fight
        "Engage" -> 0
        "Investigate" -> 0
        "Draw" -> 0
        "Move" -> 0
        "Evade" -> 0
        "Parley" -> 0
        "Resign" -> 0
        else -> R.string.unknown
    }

fun getLocalizedTrait(code: String) =
    when (code) {
        "fast" -> R.string.fast
        else -> R.string.unknown
    }

fun getLocalizedSlot(code: String) =
    when (code) {
        "hand" -> R.string.hand
        "hand x2" -> R.string.hand_x2
        "accessory" -> R.string.accessory
        "ally" -> R.string.ally
        "arcane" -> R.string.arcane
        "arcane x2" -> R.string.arcane_x2
        "head" -> R.string.head
        "body" -> R.string.body
        "tarot" -> R.string.tarot
        "other" -> R.string.other
        else -> R.string.unknown
    }

fun getLocalizedUse(code: String) =
    when (code) {
        "aether" -> R.string.aether
        "charges" -> R.string.charges
        else -> R.string.unknown
    }

fun getLocalizedSkill(code: String) =
    when (code) {
        "agility" -> R.string.agility
        "combat" -> R.string.combat
        "intellect" -> R.string.intellect
        "willpower" -> R.string.willpower
        else -> R.string.unknown
    }