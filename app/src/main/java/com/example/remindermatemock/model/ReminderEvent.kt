package com.example.remindermatemock.model

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime

private const val TAG = "ReminderEvent"

sealed class ReminderEvent {
    data class AddRecurringReminder(val recurringReminder: RecurringReminder): ReminderEvent()
    data class DeleteRecurringReminder(val recurringReminder: RecurringReminder): ReminderEvent()
    data class MarkCompleted(val reminderId: Int): ReminderEvent()
    data class SnoozeReminder(val reminderId: Int, val updatedDue: LocalDateTime): ReminderEvent()
    data class DeleteReminder(val reminderId: Int): ReminderEvent()

    fun onEvent(reminders: MutableStateFlow<List<Reminder>>) {
        when (this) {
            is AddRecurringReminder -> {
                val rec = this.recurringReminder
                if (rec.id == 0) { // Add
                    val id = if (reminders.value.isEmpty()) 1 else reminders.value.last().id + 1
                    val rem = rec.copy(id).convert()
                    reminders.update { currentList -> currentList + rem }
                } else { // Update
                    reminders.update { currentReminders ->
                        // Create a NEW list with the updated item
                        currentReminders.map { reminder ->
                            if (reminder.id == rec.id) {
                                Log.d(TAG, "Updating Reminder: ${reminder.id}")
                                // Use .copy() to create a new object with the isCompleted value flipped
                                rec.convert()
                            } else {
                                reminder
                            }
                        }
                    }
                }

            }
            is DeleteRecurringReminder -> {
                reminders.update { currentReminders ->
                    Log.d(TAG, "Reminder: Deleting reminder ${this.recurringReminder.id}")
                    currentReminders.filter { reminder -> reminder.id != this.recurringReminder.id }
                }
            }
            is MarkCompleted -> {
                reminders.update { currentReminders ->
                    // Create a NEW list with the updated item
                    currentReminders.map { reminder ->
                        if (reminder.id == this.reminderId) {
                            Log.d(TAG, "Reminder: ${this.reminderId} toggleCompletion: ${reminder.isCompleted}")
                            // Use .copy() to create a new object with the isCompleted value flipped
                            reminder.copy(isCompleted = !reminder.isCompleted)
                        } else {
                            reminder
                        }
                    }
                }
            }
            is SnoozeReminder -> {
                reminders.update { currentReminders ->
                    // Create a NEW list with the updated item
                    currentReminders.map { reminder ->
                        if (reminder.id == this.reminderId) {
                            Log.d(TAG, "Snoozing Reminder: ${this.reminderId} updatedDue: ${this.updatedDue}")
                            // Use .copy() to create a new object with the isCompleted value flipped
                            reminder.copy(due = this.updatedDue)
                        } else {
                            reminder
                        }
                    }
                }
            }
            is DeleteReminder -> {
                reminders.update { currentReminders ->
                    Log.d(TAG, "Reminder: Deleting reminder ${this.reminderId}")
                    currentReminders.filter { reminder -> reminder.id != this.reminderId }
                }
            }
        }
    }
}
