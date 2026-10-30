package com.arkhamcompanion.domain.objects

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

object TimestampNormalizer {

    private val localDateTimeFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getCurrentDateTime(): String {
        return Clock.System.now().toString()
    }

    private fun parseTimestamp(value: String?): Instant? {
        if (value.isNullOrBlank()) return null

        return runCatching {
            runCatching {
                Instant.parse(value)
            }.getOrElse {
                LocalDateTime.parse(
                    value,
                    localDateTimeFormatter
                ).toInstant(ZoneOffset.UTC)
            }
        }.getOrNull()
    }

    /**
    * Returns true if timestamp1 is before timestamp2
    */
    fun compareTimestamps(timestamp1: String?, timestamp2: String?): Boolean {
        val first = parseTimestamp(timestamp1) ?: return false
        val second = parseTimestamp(timestamp2) ?: return false

        return first.isBefore(second)
    }

    fun isAtLeastTwoWeeksApart(timestamp1: String?, timestamp2: String?): Boolean {
        val first = parseTimestamp(timestamp1) ?: return false
        val second = parseTimestamp(timestamp2) ?: return false

        return Duration.between(first, second).abs() >= Duration.ofDays(14)
    }
}