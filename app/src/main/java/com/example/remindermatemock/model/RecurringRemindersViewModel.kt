package com.example.remindermatemock.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class RecurringRemindersViewModel : ViewModel() {

    // Private state for the complete list of reminders
    private val _allRecurringReminders = MutableStateFlow<List<RecurringReminder>>(emptyList())

    // Private state for the currently selected filter. Null represents "All".
    private val _selectedFilter = MutableStateFlow<IntervalUnit?>(null)
    val selectedFilter: StateFlow<IntervalUnit?> = _selectedFilter.asStateFlow()

    // Publicly exposed, filtered list of reminders for the UI to observe.
    // It combines the full list and the filter, and re-emits a new list when either changes.
    val filteredRecurringReminders: StateFlow<List<RecurringReminder>> =
        _allRecurringReminders.combine(_selectedFilter) { recurringReminders, filter ->
            if (filter == null) {
                // If filter is null, show all recurringReminders
                recurringReminders
            } else {
                // Otherwise, filter by the selected interval unit
                recurringReminders.filter { rr -> rr.recurrences.any {  it.intervalUnit == filter }}
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEvent(event: ReminderEvent) {
        event.onRecurringReminderEvent(_allRecurringReminders)
    }
    init {
        // Load some mock data when the ViewModel is created
        loadMockReminders()
    }

    /**
     * Public function for the UI to call when the user selects a new filter.
     */
    fun onFilterChanged(newFilter: IntervalUnit?) {
        _selectedFilter.value = newFilter
    }

    private fun loadMockReminders() {
        _allRecurringReminders.value = SampleRecurringReminders()
    }
}