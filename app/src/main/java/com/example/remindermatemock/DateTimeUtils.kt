package com.example.remindermatemock

// DateTimeUtils.kt
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Formats a kotlinx.datetime.LocalDateTime into a user-friendly, localized string.
 * Returns an empty string if the dateTime is null.
 */
fun formatKotlinxDateTime(dateTime: LocalDateTime?): String {
    if (dateTime == null) return ""
    val javaDateTime = dateTime.toJavaLocalDateTime()
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    return javaDateTime.format(formatter)
}