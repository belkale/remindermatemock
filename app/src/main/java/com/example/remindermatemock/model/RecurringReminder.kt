package com.example.remindermatemock.model

data class RecurringReminder(
    val id: Int,
    val title: String,
    val description: String,
    val recurrences: List<Recurrence>
)