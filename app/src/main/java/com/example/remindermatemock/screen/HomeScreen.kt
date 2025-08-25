package com.example.remindermatemock.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remindermatemock.model.IntervalUnit
import com.example.remindermatemock.model.Recurrence
import com.example.remindermatemock.model.RecurringReminder
import com.example.remindermatemock.model.ReminderEvent
import com.example.remindermatemock.model.ReminderViewModel
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme
import com.example.remindermatemock.widget.HelpDialog
import com.example.remindermatemock.widget.MainMenu
import com.example.remindermatemock.widget.RecurringReminderForm
import com.example.remindermatemock.widget.RemindersWidget
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val TAG = "Home"
private val dateFormat = LocalDate.Format {
    day()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED) // "Aug"
    chars(", ")                            // ", "
    year()                                 // "2025"
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HomeScreen(reminderViewModel: ReminderViewModel = viewModel()) {
    // Also collect the state of the toggle itself to update the button's UI
    val showCompleted by reminderViewModel.showCompleted.collectAsState()
    val reminders by reminderViewModel.reminders.collectAsState()
    val selectedDate by reminderViewModel.selectedDate.collectAsState()
    var reminderToEdit by remember { mutableStateOf<RecurringReminder?>(null) }

    var menuExpanded by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFormDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Reminder Mate") },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { menuExpanded = !menuExpanded }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                        MainMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "Help")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround, // This is the key
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Calendar")
                    }
                    Text(selectedDate.format(dateFormat))

                    IconButton(onClick = {
                        Log.d(TAG, "showCompletedToggled: $showCompleted")
                        reminderViewModel.onEvent(ReminderEvent.ShowCompleted)
                    }) {
                        if (showCompleted)
                            Icon(Icons.Filled.Check, contentDescription = "Hide Completed")
                        else
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Show Completed")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showFormDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        RemindersWidget(
            reminders,
            onCheckedChange = { reminderId ->
                // This is the "event flowing up"
                reminderViewModel.onEvent(ReminderEvent.MarkCompleted(reminderId))
            },
            onSnoozeReminder = { reminderId, newDue ->
                reminderViewModel.onEvent(ReminderEvent.SnoozeReminder(reminderId, newDue))
            },
            onDeleteReminder = { reminderId ->
                reminderViewModel.onEvent(ReminderEvent.DeleteReminder(reminderId))
            },
            onUpdateReminder = { reminderId ->
                val rem = reminders.find { it.id == reminderId }
                if (rem != null) {
                    reminderToEdit = RecurringReminder(rem.id, rem.name, rem.description,
                        listOf(Recurrence(rem.due, null, 0, IntervalUnit.NONE)))
                    showFormDialog = true
                }

            },
            modifier = Modifier
                .padding(innerPadding)
        )
    }
    // This part of the code will only run (compose) when showHelpDialog is true.
    if (showHelpDialog) {
        HelpDialog(
            onDismiss = {
                // This event sets the state to false, hiding the dialog
                showHelpDialog = false
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled by remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    enabled = confirmEnabled,
                    onClick = {
                        // Get the selected date in milliseconds, convert it, and send to ViewModel
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val date = Instant.fromEpochMilliseconds(selectedMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            reminderViewModel.onEvent(ReminderEvent.SelectDate(date))
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showFormDialog) {
        // Use the Dialog composable for custom content
        Dialog(onDismissRequest = { showFormDialog = false }) {
            // Surface provides a background color, shape, and elevation for the dialog's content
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp
            ) {
                RecurringReminderForm(
                    existingReminder = reminderToEdit,
                    onConfirm = { rec ->
                        // Logic to save or update the reminder in your ViewModel or repository
                        Log.d(TAG, "SAVING: $rec")
                        reminderViewModel.onEvent(ReminderEvent.AddRecurringReminder(rec))
                        showFormDialog = false
                    },
                    onCancel = {
                        showFormDialog = false
                    }
                )
            }
        }
    }
}
