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

class ReminderViewModel : ViewModel() {

    // Source of truth for the filter state
    private val _showCompleted = MutableStateFlow(true) // Default to hiding completed
    val showCompleted: StateFlow<Boolean> = _showCompleted.asStateFlow()

    @OptIn(ExperimentalTime::class)
    private val _selectedDate = MutableStateFlow<LocalDate>(Clock.System.todayIn(
        TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Private mutable state flow that can be updated only within the ViewModel
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())

    // Public, read-only state flow for the UI to observe
    val reminders: StateFlow<List<Reminder>> = combine(_reminders, _showCompleted) { reminderList, completedBool ->
        if (completedBool) {
            reminderList // If true, return the full list
        } else {
            reminderList.filter { !it.isCompleted } // If false, return only incomplete ones
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

    // Central function to handle all UI events
    fun onEvent(event: ReminderEvent) {
        event.onEvent(_reminders)
    }

    fun showCompletedToggled() {
        _showCompleted.update { !it }
    }

    fun onSelectedDateChanged(date: LocalDate) {
        Log.d(TAG, "setDateFilter: $date")
        _selectedDate.update {  date }
    }

    private fun loadSampleReminders() {
        _reminders.value = SampleData(5)
    }
}