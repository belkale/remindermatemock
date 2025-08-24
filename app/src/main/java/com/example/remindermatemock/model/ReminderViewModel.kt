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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "ReminderViewModel"
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

    // This function now just updates the filter state.
    // The `visibleReminders` flow will react automatically.
    fun toggleShowCompleted() {
        _showCompleted.update { !it } // Toggles the boolean value
    }

    fun setDateFilter(date: LocalDate) {
        Log.d(TAG, "setDateFilter: $date")
        _selectedDate.value = date
    }

    // This is the "event" handler that the UI will call
    fun toggleCompletion(reminderId: Int) {
        _reminders.update { currentReminders ->
            // Create a NEW list with the updated item
            currentReminders.map { reminder ->
                if (reminder.id == reminderId) {
                    Log.d(TAG, "Reminder: $reminderId toggleCompletion: ${reminder.isCompleted}")
                    // Use .copy() to create a new object with the isCompleted value flipped
                    reminder.copy(isCompleted = !reminder.isCompleted)
                } else {
                    reminder
                }
            }
        }
    }

    private fun loadSampleReminders() {
        _reminders.value = SampleData()
    }
}