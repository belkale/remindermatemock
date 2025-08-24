package com.example.remindermatemock.model

import kotlinx.datetime.LocalDateTime

enum class IntervalUnit {
    NONE, MINUTE, HOUR, DAY, WEEK, WEEK_MONTH, MONTH, YEAR
}
data class Recurrence(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val repeatInterval: Int,
    val intervalUnit: IntervalUnit
)