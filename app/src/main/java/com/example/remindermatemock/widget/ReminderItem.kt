package com.example.remindermatemock.widget

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindermatemock.addMinsFromNow
import com.example.remindermatemock.model.Reminder
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

private const val TAG = "ReminderItem"
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ReminderItem(
    reminder: Reminder,
    onCheckedChange: () -> Unit,
    onSnooze: (newDueTime: LocalDateTime) -> Unit,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    var isMenuExpanded by remember(reminder.id) { mutableStateOf(false) }
    // val context = LocalContext.current // No longer needed here for the pickers

    // State to control the visibility of our custom date-time picker dialog
    var showCustomSnoozeDialog by remember(reminder.id) { mutableStateOf(false) }

    val textStyle = if (reminder.isCompleted) {
        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    ListItem(
        leadingContent = {
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = { onCheckedChange() }
            )
        },
        headlineContent = { Text(reminder.name, style = textStyle) },
        supportingContent = { if (reminder.description.isNotEmpty()) Text(reminder.description) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(com.example.remindermatemock.formatTime(reminder.due.time)) // Consider formatting this better
                Spacer(Modifier.width(4.dp))
                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options for reminder: ${reminder.name}"
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Snooze 30 mins") },
                            onClick = {
                                Log.d(TAG, "Snooze 30 mins clicked for ID: ${reminder.id}") // Add log
                                isMenuExpanded = false
                                onSnooze(addMinsFromNow(30))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Snooze (Custom)") },
                            onClick = {
                                Log.d(TAG, "Snooze (Custom) clicked for ID: ${reminder.id}") // Add log
                                isMenuExpanded = false
                                showCustomSnoozeDialog = true // Trigger our custom dialog
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Update") },
                            onClick = {
                                Log.d(TAG, "Update clicked for ID: ${reminder.id}") // Add log
                                onUpdate()
                                isMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                Log.d(TAG, "Delete clicked for ID: ${reminder.id}") // Add log
                                onDelete()
                                isMenuExpanded = false
                            }
                        )
                        // Add "Update" DropdownMenuItem when ready
                    }
                }
            }
        }
    )

    // Use the custom Date Time Picker Dialog
    if (showCustomSnoozeDialog) {
        CustomDateTimePickerDialog(
            showDialog = true, // This is already true if we are in this block
            initialDateTime = addMinsFromNow(5), // Start with the reminder's current due time
            onDateTimeSelected = { newSnoozeDateTime ->
                onSnooze(newSnoozeDateTime)
                showCustomSnoozeDialog = false // Dismiss our controlling state
            },
            onDismiss = {
                showCustomSnoozeDialog = false // Dismiss our controlling state
            }
        )
    }
}

@Preview
@Composable
fun ReminderItemPreview() {
    ReminderMateMockTheme {
        ReminderItem(
            Reminder(1, "Take the dog for walk",
                "Go around the apartment at 6 pm",
                addMinsFromNow(10),false),
            onCheckedChange = {},onSnooze = {},onDelete = {},onUpdate = {})
    }
}