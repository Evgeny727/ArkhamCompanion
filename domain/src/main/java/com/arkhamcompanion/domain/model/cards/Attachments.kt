package com.arkhamcompanion.domain.model.cards

import com.arkhamcompanion.domain.enums.CardType
import com.arkhamcompanion.domain.model.decks.DeckSlots

data class Attachments(
    val code: String,
    val limit: Int?,
    val traits: Set<String>,
    val filters: List<AttachmentFilter>,
    val name: String,
    val icon: AttachmentIcon,
    val targetSize: Int,
    val requiredCards: DeckSlots
)

sealed interface AttachmentFilter {
    val not: Boolean

    data class Type(
        val value: CardType,
        override val not: Boolean
    ) : AttachmentFilter

    data class Xp(
        val value: Int?,
        override val not: Boolean
    ) : AttachmentFilter
}

sealed interface AttachmentIcon {
    data object Package : AttachmentIcon
    data object Lightbulb : AttachmentIcon
    data object Store : AttachmentIcon
    data object Wand : AttachmentIcon
    data object Stamp : AttachmentIcon
    data class Link(val link: String) : AttachmentIcon

    companion object {
        fun byIcon(icon: String): AttachmentIcon = when (icon) {
            "package" -> Package
            "lightbulb" -> Lightbulb
            "store" -> Store
            "wand" -> Wand
            "stamp" -> Stamp
            else -> Link(icon)
        }
    }
}
