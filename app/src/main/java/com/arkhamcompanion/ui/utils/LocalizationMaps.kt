package com.arkhamcompanion.ui.utils

import com.arkhamcompanion.R

fun getLocalizedAction(code: String) =
    when (code) {
        "Fight" -> R.string.fight
        "Engage" -> R.string.engage
        "Investigate" -> R.string.investigate
        "Draw" -> R.string.draw_action
        "Resource" -> R.string.resource_action
        "Move" -> R.string.move
        "Evade" -> R.string.evade
        "Parley" -> R.string.parley
        "Resign" -> R.string.resign
        else -> R.string.unknown
    }

fun getLocalizedTrait(code: String) =
    when (code) {
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
        "aether" -> R.string.uses_aether
        "arrow" -> R.string.uses_arrow
        "ammo" -> R.string.uses_ammo
        "blame" -> R.string.uses_blame
        "bounties" -> R.string.uses_bounties
        "brilliance" -> R.string.uses_brilliance
        "chances" -> R.string.uses_chances
        "charges" -> R.string.uses_charges
        "doses" -> R.string.uses_doses
        "discoveries" -> R.string.uses_discoveries
        "durability" -> R.string.uses_durability
        "evidence" -> R.string.uses_evidence
        "inspiration" -> R.string.uses_inspiration
        "keys" -> R.string.uses_keys
        "leylines" -> R.string.uses_leylines
        "locks" -> R.string.uses_locks
        "obsessions" -> R.string.uses_obsessions
        "obligations" -> R.string.uses_obligations
        "offerings" -> R.string.uses_offerings
        "poems" -> R.string.uses_poems
        "portents" -> R.string.uses_portents
        "portions" -> R.string.uses_portions
        "resources" -> R.string.uses_resources
        "renown" -> R.string.uses_renown
        "rumors" -> R.string.uses_rumors
        "samples" -> R.string.uses_samples
        "secrets" -> R.string.uses_secrets
        "signs" -> R.string.uses_signs
        "supplies" -> R.string.uses_supplies
        "shards" -> R.string.uses_shards
        "shell" -> R.string.uses_shell
        "tickets" -> R.string.uses_tickets
        "time" -> R.string.uses_time
        "treats" -> R.string.uses_treats
        "tries" -> R.string.uses_tries
        "truths" -> R.string.uses_truths
        "wishes" -> R.string.uses_wishes
        "whistles" -> R.string.uses_whistles
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