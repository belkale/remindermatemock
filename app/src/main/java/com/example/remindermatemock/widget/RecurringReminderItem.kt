package com.example.remindermatemock.widget

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.remindermatemock.addMinsFromNow
import com.example.remindermatemock.formatKotlinxDateTime
import com.example.remindermatemock.model.IntervalUnit
import com.example.remindermatemock.model.Recurrence
import com.example.remindermatemock.model.RecurringReminder
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme

private const val TAG = "RecurringReminderItem"

@Composable
fun RecurringReminderItem(
    recurringReminder: RecurringReminder,
    onDelete: () -> Unit,
    onUpdate: (rec: RecurringReminder) -> Unit,
) {
    var isMenuExpanded by remember(recurringReminder.id) { mutableStateOf(false) }
    var rrToEdit by remember(recurringReminder.id) { mutableStateOf(recurringReminder) }
    var showFormDialog by remember(recurringReminder.id) { mutableStateOf(false) }
    ListItem(
        headlineContent = { Text(text = recurringReminder.title, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = { Text(text = generateRecurringReminderItemSupportingContent(recurringReminder))},
        trailingContent = {
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options for reminder: ${recurringReminder.title}"
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Update") },
                        onClick = {
                            Log.d(TAG, "Update clicked for ID: ${recurringReminder.id}") // Add log
                            isMenuExpanded = false
                            showFormDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            Log.d(TAG, "Delete clicked for ID: ${recurringReminder.id}") // Add log
                            onDelete()
                            isMenuExpanded = false
                        }
                    )
                }
            }
        }
    )

    if (showFormDialog) {
        RecurringReminderFormDialog(
            rrToEdit,
            onDismissRequest = { showFormDialog = false },
            onConfirm = { rec ->
                // Logic to save or update the reminder in your ViewModel or repository
                Log.d(TAG, "SAVING: $rec")
                onUpdate(rec)
                showFormDialog = false
            },
            onCancel = {
                showFormDialog = false
            }
        )
    }
}

fun generateRecurringReminderItemSupportingContent(rr: RecurringReminder): String {
    val description = if (rr.description.isEmpty()) "" else "${rr.description}\n"
    val starts = "Starts: ${formatKotlinxDateTime(rr.recurrences[0].startTime)}"
    val ends = if (rr.recurrences[0].endTime != null) {
        "Ends: ${formatKotlinxDateTime(rr.recurrences[0].endTime)}"
    } else {
        ""
    }
    val repeats = "Repeats: ${
        formatRepeatText(
            rr.recurrences[0].repeatInterval,
            rr.recurrences[0].intervalUnit
        )
    }"
    return "$description$starts $ends\n$repeats"
}
// Helper function to create a readable repeat string
private fun formatRepeatText(interval: Int, unit: IntervalUnit): String {
    if (unit == IntervalUnit.NONE) return "Does not repeat"
    val unitStr = unit.name
    return if (interval == 1) "Every $unitStr" else "Every $interval ${unitStr}s"
}

@Preview
@Composable
fun RecurringReminderItemPreview() {
    ReminderMateMockTheme {
        RecurringReminderItem(
            RecurringReminder(
                id = 1,
                title = "Pay electricity bill",
                description = "Pay the electricity bill for the month",
                listOf(Recurrence(addMinsFromNow(10), null, 1, IntervalUnit.DAY))
            ),
            onDelete = {},
            onUpdate = {}
        )
    }
}