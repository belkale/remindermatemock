package com.example.remindermatemock.model

import com.example.remindermatemock.addMinsFromNow
import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime

fun SampleData(targetSize: Int):List<Reminder> {
    val seedList = listOf(
        Reminder(0, "Drink 2 glasses of water", "",
            LocalDateTime(2023, 8, 23, 8, 0)),
        Reminder(0, "Walk the dog", "Take Leo for a walk",
            LocalDateTime(2023, 8, 23, 9, 0)),
        Reminder(0, "Buy groceries", "",
            LocalDateTime(2023, 8, 23, 10, 0)),
        Reminder(0, "Read a book", "Read Lord Of The Rings",
            LocalDateTime(2023, 8, 23, 11, 0)),
        Reminder(0, "Exercise", "Do strength training",
            LocalDateTime(2023, 8, 23, 12, 0))
    )
    return List(targetSize) { index -> seedList[index % seedList.size].copy(id = index + 1) }
}

fun SampleRecurringReminders() = listOf(
    RecurringReminder(1, "Take out the trash", "Weekly chore",
        listOf(Recurrence(addMinsFromNow(60), null, 1, IntervalUnit.WEEK))),
    RecurringReminder(2, "Daily Stand-up", "Team meeting",
        listOf(Recurrence(addMinsFromNow(30), null, 1, IntervalUnit.DAY))),
    RecurringReminder(3, "Pay Rent", "Monthly bill",
        listOf(Recurrence(addMinsFromNow(5*24*60), null, 1, IntervalUnit.MONTH))),
    RecurringReminder(4, "Water Plants", "Keep the office green",
        listOf(Recurrence(addMinsFromNow(2*60), null, 3, IntervalUnit.DAY))),
    RecurringReminder(5, "Annual Review", "Performance review with manager",
        listOf(Recurrence(addMinsFromNow(6*30*24*60), null, 1, IntervalUnit.YEAR))),
    RecurringReminder(6, "Check Emails", "Every 30 minutes",
        listOf(Recurrence(addMinsFromNow(0), null, 30, IntervalUnit.MINUTE))),
    RecurringReminder(7, "Project Sync", "Another weekly meeting",
        listOf(Recurrence(addMinsFromNow(2*24*60), null, 1, IntervalUnit.WEEK))),
    RecurringReminder(8, "One-off Task", "No repeat",
        listOf(Recurrence(addMinsFromNow(24*60), null, 0, IntervalUnit.NONE)))
)
