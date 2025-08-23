package com.example.remindermatemock.model

import kotlinx.datetime.LocalDateTime

data class Reminder(val id: Int, val name: String, val description: String, val due: LocalDateTime,
                    val isCompleted: Boolean = false)