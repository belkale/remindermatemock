package com.example.remindermatemock.model

data class RecurringReminder(
    val id: Int,
    val title: String,
    val description: String,
    val recurrences: List<Recurrence>
) {
    fun convert() = Reminder(id, title, description, recurrences.first().startTime)
}