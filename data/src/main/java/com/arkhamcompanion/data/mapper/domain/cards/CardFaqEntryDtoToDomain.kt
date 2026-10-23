package com.arkhamcompanion.data.mapper.domain.cards

import com.arkhamcompanion.data.remote.dto.CardFaqEntryDto
import com.arkhamcompanion.data.utils.preprocessCardText
import com.arkhamcompanion.domain.model.cards.CardFaqEntry
import com.arkhamcompanion.domain.objects.CardTextParser
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

internal fun CardFaqEntryDto.toDomain(): CardFaqEntry = CardFaqEntry(
    id = id,
    question = CardTextParser.parse(
        "<i>$question</i>".preprocessCardText(processWebFormat = true)
    ),
    ruling = CardTextParser.parse(
        ruling.preprocessCardText(processWebFormat = true)
    ),
    citation = citation,
    position = position,
    date = date?.formatRulesFormDate()
)

private fun String.formatRulesFormDate(): String {
    val date = LocalDate.parse(this)

    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())

    return date.format(formatter)
}