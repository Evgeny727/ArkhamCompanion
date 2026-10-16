package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.data.remote.dto.CardFaqEntryDto
import com.arkhamcompanion.data.utils.preprocessCardText
import com.arkhamcompanion.domain.model.cards.CardFaqEntry
import com.arkhamcompanion.domain.objects.CardTextParser

internal fun CardFaqEntryDto.toDomain(): CardFaqEntry = CardFaqEntry(
    id = id,
    question = CardTextParser.parse(
        "<i>$question</i>".preprocessCardText(processWebFormat = true)
    ),
    ruling = CardTextParser.parse(
        ruling.preprocessCardText(processWebFormat = true)
    ),
    citation = citation,
    position = position
)