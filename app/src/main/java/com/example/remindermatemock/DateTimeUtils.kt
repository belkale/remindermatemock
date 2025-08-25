package com.example.remindermatemock

// DateTimeUtils.kt
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

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

@OptIn(ExperimentalTime::class)
fun addMinsFromNow(mins: Int): LocalDateTime =
    now().plus(mins, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.currentSystemDefault())

fun formatTime(time: LocalTime): String {
    val hour = time.hour.toString().padStart(2, '0')
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}