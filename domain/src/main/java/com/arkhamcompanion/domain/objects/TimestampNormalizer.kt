package com.arkhamcompanion.domain.objects

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.time.Duration.Companion.days

object TimestampNormalizer {

    fun getCurrentDateTime(): String {
        // Get the current time
        val now = Date()
        // Create a formatter using the pattern "yyyy-MM-dd'T'HH:mm:ss.SSS"
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val formatted = sdf.format(now)

        return formatted
    }

    // It ensures the fraction has exactly 3 digits.
    fun fixFraction(dateString: String?): String? {
        if (dateString == null) return null
        // Regex groups:
        // 1. The part before the fraction (including the dot)
        // 2. The fractional digits
        // 3. The rest (timezone information)
        val regex = """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.)(\d+)(.*)""".toRegex()
        return regex.replace(dateString) { matchResult ->
            val prefix = matchResult.groupValues[1]
            val fraction = matchResult.groupValues[2]
            val suffix = matchResult.groupValues[3]
            // Truncate to the first 3 digits or pad with zeros if needed.
            val fixedFraction = when {
                fraction.length > 3 -> fraction.substring(0, 3)
                fraction.length < 3 -> fraction.padEnd(3, '0')
                else -> fraction
            }
            prefix + fixedFraction + suffix
        }
    }

    fun compareTimestamps(timestamp1: String?, timestamp2: String?): Boolean {
        if (timestamp1.isNullOrBlank() || timestamp2.isNullOrBlank()) return true
        val timestamp1Fixed = fixFraction(timestamp1)
        val timestamp2Fixed = fixFraction(timestamp2)

        // Define the date format
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        // Parse the timestamps
        val date1: Date? = format.parse(timestamp1Fixed)
        val date2: Date? = format.parse(timestamp2Fixed)

        if (date1 == null || date2 == null) return true

        return date1.before(date2)
    }

    fun isAtLeastTwoWeeksApart(timestamp1: String?, timestamp2: String?): Boolean {
        if (timestamp1.isNullOrBlank() || timestamp2.isNullOrBlank()) return false

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val date1 = format.parse(fixFraction(timestamp1) ?: return false) ?: return false
        val date2 = format.parse(fixFraction(timestamp2) ?: return false) ?: return false

        return abs(date2.time - date1.time).days >= 14.days
    }
}