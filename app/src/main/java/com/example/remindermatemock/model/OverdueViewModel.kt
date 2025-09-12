package com.example.remindermatemock.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.filter

class OverdueViewModel : ViewModel() {
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val overdueIncompleteReminders: StateFlow<List<Reminder>> = _reminders
        .map { remindersList ->
            remindersList.filter { reminder -> !reminder.isCompleted }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Or Lazily, Eagerly
            initialValue = _reminders.value.filter { !it.isCompleted } // Initial value consistent with the map
        )

    init {
        // Load some initial sample data
        loadSampleReminders()
    }

    // Central function to handle all UI events
    fun onEvent(event: ReminderEvent) {
        event.onEvent(_reminders)
    }

    private fun loadSampleReminders() {
        _reminders.value = SampleData(5)
    }
}