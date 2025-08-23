package com.example.remindermatemock.model

import kotlinx.datetime.LocalDateTime

fun SampleData():List<Reminder> {
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
    val targetSize = 20
    return List(targetSize) { index -> seedList[index % seedList.size].copy(id = index + 1) }
}