package com.example.remindermatemock.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "ReminderViewModel"

sealed class ReminderEvent {
    data class AddRecurringReminder(val recurringReminder: RecurringReminder): ReminderEvent()
    data class DeleteRecurringReminder(val recurringReminder: RecurringReminder): ReminderEvent()
    data class MarkCompleted(val reminderId: Int): ReminderEvent()
    data class SnoozeReminder(val reminderId: Int, val updatedDue: LocalDateTime): ReminderEvent()
    data class DeleteReminder(val reminderId: Int): ReminderEvent()
    object ShowCompleted: ReminderEvent()
    data class SelectDate(val date: LocalDate): ReminderEvent()
}
class ReminderViewModel : ViewModel() {

    // Source of truth for the filter state
    private val _showCompleted = MutableStateFlow(false) // Default to hiding completed
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    @OptIn(ExperimentalTime::class)
    private val _selectedDate = MutableStateFlow<LocalDate>(Clock.System.todayIn(
        TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Private mutable state flow that can be updated only within the ViewModel
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())

    // Public, read-only state flow for the UI to observe
    val reminders: StateFlow<List<Reminder>> = combine(_reminders, _showCompleted) { reminders, showCompleted ->
        if (showCompleted) {
            reminders // If true, return the full list
        } else {
            reminders.filter { !it.isCompleted } // If false, return only incomplete ones
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<Reminder>()
    )

    init {
        // Load some initial sample data
        loadSampleReminders()
    }

    fun convert(rec: RecurringReminder) = Reminder(rec.id, rec.title,
        rec.description, rec.recurrences.first().startTime)

    // Central function to handle all UI events
    fun onEvent(event: ReminderEvent) {
        when (event) {
            is ReminderEvent.AddRecurringReminder -> {
                val rec = event.recurringReminder
                if (rec.id == 0) { // Add
                    val id = if (_reminders.value.isEmpty()) 1 else _reminders.value.last().id + 1
                    val rem = convert(rec.copy(id))
                    _reminders.update { currentList -> currentList + rem }
                } else { // Update
                    _reminders.update { currentReminders ->
                        // Create a NEW list with the updated item
                        currentReminders.map { reminder ->
                            if (reminder.id == rec.id) {
                                Log.d(TAG, "Updating Reminder: ${reminder.id}")
                                // Use .copy() to create a new object with the isCompleted value flipped
                                convert(rec)
                            } else {
                                reminder
                            }
                        }
                    }
                }

            }
            is ReminderEvent.DeleteRecurringReminder -> {
                _reminders.update { currentReminders ->
                    Log.d(TAG, "Reminder: Deleting reminder ${event.recurringReminder.id}")
                    currentReminders.filter { reminder -> reminder.id != event.recurringReminder.id }
                }
            }
            is ReminderEvent.MarkCompleted -> {
                _reminders.update { currentReminders ->
                    // Create a NEW list with the updated item
                    currentReminders.map { reminder ->
                        if (reminder.id == event.reminderId) {
                            Log.d(TAG, "Reminder: ${event.reminderId} toggleCompletion: ${reminder.isCompleted}")
                            // Use .copy() to create a new object with the isCompleted value flipped
                            reminder.copy(isCompleted = !reminder.isCompleted)
                        } else {
                            reminder
                        }
                    }
                }
            }
            is ReminderEvent.SnoozeReminder -> {
                _reminders.update { currentReminders ->
                    // Create a NEW list with the updated item
                    currentReminders.map { reminder ->
                        if (reminder.id == event.reminderId) {
                            Log.d(TAG, "Snoozing Reminder: ${event.reminderId} updatedDue: ${event.updatedDue}")
                            // Use .copy() to create a new object with the isCompleted value flipped
                            reminder.copy(due = event.updatedDue)
                        } else {
                            reminder
                        }
                    }
                }
            }
            is ReminderEvent.DeleteReminder -> {
                _reminders.update { currentReminders ->
                    Log.d(TAG, "Reminder: Deleting reminder ${event.reminderId}")
                    currentReminders.filter { reminder -> reminder.id != event.reminderId }
                }
            }
            is ReminderEvent.ShowCompleted -> {
                _showCompleted.update { !it }
            }
            is ReminderEvent.SelectDate -> {
                Log.d(TAG, "setDateFilter: ${event.date}")
                _selectedDate.update {  event.date }
            }

        }
    }

    private fun loadSampleReminders() {
        _reminders.value = SampleData(20)
    }
}