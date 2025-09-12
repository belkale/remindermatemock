package com.example.remindermatemock.screen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.remindermatemock.model.IntervalUnit
import com.example.remindermatemock.model.OverdueModel
import com.example.remindermatemock.model.Recurrence
import com.example.remindermatemock.model.RecurringReminder
import com.example.remindermatemock.model.ReminderEvent
import com.example.remindermatemock.widget.RecurringReminderForm
import com.example.remindermatemock.widget.RemindersWidget

private const val TAG = "OverdueScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverdueScreen(
    navController: NavController, // Use the actual NavController
    overdueModel: OverdueModel = viewModel()
) {
    val overdueReminders by overdueModel.overdueIncompleteReminders.collectAsState()
    var reminderToEdit by remember { mutableStateOf<RecurringReminder?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overdue Reminders") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Use NavController to go back
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        RemindersWidget(
            overdueReminders,
            onCheckedChange = { reminderId ->
                // This is the "event flowing up"
                overdueModel.onEvent(ReminderEvent.MarkCompleted(reminderId))
            },
            onSnoozeReminder = { reminderId, newDue ->
                overdueModel.onEvent(ReminderEvent.SnoozeReminder(reminderId, newDue))
            },
            onDeleteReminder = { reminderId ->
                overdueModel.onEvent(ReminderEvent.DeleteReminder(reminderId))
            },
            onUpdateReminder = { reminderId ->
                val rem = overdueReminders.find { it.id == reminderId }
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
                        overdueModel.onEvent(ReminderEvent.AddRecurringReminder(rec))
                        showFormDialog = false
                        reminderToEdit = null
                    },
                    onCancel = {
                        showFormDialog = false
                        reminderToEdit = null
                    }
                )
            }
        }
    }
}
