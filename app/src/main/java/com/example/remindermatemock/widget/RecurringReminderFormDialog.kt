package com.example.remindermatemock.widget

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.remindermatemock.model.RecurringReminder

@Composable
fun RecurringReminderFormDialog(reminderToEdit: RecurringReminder?,
                                onDismissRequest: () -> Unit,
                                onConfirm: (RecurringReminder) -> Unit,
                                onCancel: () -> Unit) {
    // Use the Dialog composable for custom content
    Dialog(onDismissRequest = onDismissRequest) {
        // Surface provides a background color, shape, and elevation for the dialog's content
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            RecurringReminderForm(
                existingReminder = reminderToEdit,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }
    }
}