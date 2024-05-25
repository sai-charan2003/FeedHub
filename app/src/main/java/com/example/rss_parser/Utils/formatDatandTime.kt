package com.example.rss_parser.Utils

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateTimeFormatter {
    fun formatDateAndLocalTime(dateString: String): String {
        val patterns = arrayOf(
            "EEE, d MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss 'Z'",
            "EEE, dd MMM yyyy HH:mm z",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ss  Z",
            "yyyy-MM-dd HH:mm:ss Z",
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ssXXX",
            "EEE, d MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ss zzzz",
            "EEE, d MMM yyyy HH:mm:ss VV",
            "EEE, d MMM yyyy HH:mm:ss zzz",
            "EEE, d MMM yyyy HH:mm:ss X",
            "EEE, d MMM yyyy HH:mm:ss XXX",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss a",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss a",
            "yyyy/MM/dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "dd-MM-yyyy"
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                val temporalAccessor = formatter.parse(dateString)
                val zonedDateTime =
                    ZonedDateTime.from(temporalAccessor).withZoneSameInstant(ZoneId.systemDefault())
                val localZoneDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

                val formattedDateTime = DateTimeFormatter
                    .ofPattern("dd/MM - HH:mm")
                    .format(localZoneDateTime)

                return formattedDateTime
            } catch (e: Exception) {
                // Try the next pattern
            }
        }

        return ""
    }

    fun convertToMillisecondsEpoch(dateString: String): Long {
        val patterns = arrayOf(
            "EEE, d MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss 'Z'",
            "EEE, dd MMM yyyy HH:mm z",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ss  Z",
            "yyyy-MM-dd HH:mm:ss Z",
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ssXXX",
            "EEE, d MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ss zzzz",
            "EEE, d MMM yyyy HH:mm:ss VV",
            "EEE, d MMM yyyy HH:mm:ss zzz",
            "EEE, d MMM yyyy HH:mm:ss X",
            "EEE, d MMM yyyy HH:mm:ss XXX",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss a",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss a",
            "yyyy/MM/dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "EEE, d MMM yyyy HH:mm:ss zzz",
            "EEE, d MMM yyyy HH:mm:ss",
            "EEE MMM d HH:mm:ss zzz yyyy",
            "EEE, MMM d, yyyy hh:mm:ss a",
            "EEE, MMM d, yyyy HH:mm:ss",
            "EEE MMM d HH:mm:ss yyyy",
            "EEE, d MMM yyyy HH:mm:ss Z"
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                val temporalAccessor = formatter.parse(dateString)
                val zonedDateTime =
                    ZonedDateTime.from(temporalAccessor).withZoneSameInstant(ZoneId.systemDefault())
                val localZoneDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

                return localZoneDateTime.toInstant().toEpochMilli()
            } catch (e: Exception) {
                // Try the next pattern
            }
        }

        return 0L // Return default value if date format is not recognized
    }

    fun convertTo12HourFormatWithDayAndMonth(dateString: String): String {
        val patterns = arrayOf(
            "[EEE, d MMM yyyy HH:mm:ss z]",
            "[EEE, d MMM yyyy HH:mm:ss Z]",
            "[EEE, dd MMM yyyy HH:mm:ss 'Z']",
            "[EEE, dd MMM yyyy HH:mm z]",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd HH:mm:ss  Z",
            "yyyy-MM-dd HH:mm:ss Z",
            "yyyy-MM-dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, d MMM yyyy HH:mm:ssXXX",
            "EEE, d MMM yyyy HH:mm:ss z", // Example: Mon, 23 Jan 2023 12:45:00 EST
            "EEE, d MMM yyyy HH:mm:ss zzzz", // Example: Tue, 10 Dec 2023 14:30:00 Pacific Standard Time
            "EEE, d MMM yyyy HH:mm:ss VV", // Example: Wed, 07 Jun 2023 21:00:00 America/New_York
            "EEE, d MMM yyyy HH:mm:ss zzz", // Example: Thu, 02 Feb 2023 03:45:00 GMT+05:00
            "EEE, d MMM yyyy HH:mm:ss X", // Example: Fri, 24 Mar 2023 09:15:00 +0530
            "EEE, d MMM yyyy HH:mm:ss XXX" // Example: Sat, 18 Nov 2023 18:45:00 +05:30
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                val temporalAccessor = formatter.parse(dateString)
                val zonedDateTime =
                    ZonedDateTime.from(temporalAccessor).withZoneSameInstant(ZoneId.systemDefault())
                val localZoneDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

                val formattedDateTime = DateTimeFormatter
                    .ofPattern("dd/MM - hh:mm a", Locale.ENGLISH)
                    .format(localZoneDateTime)

                return formattedDateTime
            } catch (e: Exception) {
                // Try the next pattern
            }
        }

        return ""
    }

    fun formatDate(inputDate: String): String {
        val currentDate = LocalDateTime.now()
        val yesterdayDate = LocalDate.now().minusDays(1)

        val epochMilliseconds = convertToMillisecondsEpoch(inputDate)
        if (epochMilliseconds == 0L) {
            return "Invalid date format"
        }

        val parsedDate =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilliseconds), ZoneId.systemDefault())
        val diff = Duration.between(parsedDate, currentDate)
        val days = diff.toDays()

        return when {
            parsedDate.toLocalDate() == yesterdayDate -> "Yesterday"
            days > 0 -> {
                val plural = if (days > 1) "s" else ""
                "$days day$plural ago"
            }

            else -> {
                val hours = diff.toHours()
                val minutes = diff.toMinutes()
                val seconds = diff.seconds

                when {
                    hours > 0 -> {
                        val plural = if (hours > 1) "s" else ""
                        "$hours hour$plural ago"
                    }

                    minutes > 0 -> {
                        val plural = if (minutes > 1) "s" else ""
                        "$minutes minute$plural ago"
                    }

                    else -> {
                        val plural = if (seconds > 1) "s" else ""
                        "$seconds second$plural ago"
                    }
                }
            }
        }
    }
}
