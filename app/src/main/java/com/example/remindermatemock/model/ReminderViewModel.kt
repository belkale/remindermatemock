package com.example.remindermatemock.model

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "ReminderViewModel"
class ReminderViewModel : ViewModel() {

    // Private mutable state flow that can be updated only within the ViewModel
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())

    // Public, read-only state flow for the UI to observe
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    init {
        // Load some initial sample data
        loadSampleReminders()
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