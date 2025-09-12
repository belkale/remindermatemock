package com.example.remindermatemock.widget

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.remindermatemock.model.Reminder
import com.example.remindermatemock.model.SampleData
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme
import kotlinx.datetime.LocalDateTime

private const val TAG = "RemindersWidget"
@Composable
fun RemindersWidget(
    reminders: List<Reminder>,
    onCheckedChange: (Int) -> Unit,
    onSnoozeReminder: (id: Int, newDueTime: LocalDateTime) -> Unit,
    onDeleteReminder: (id: Int) -> Unit,
    onUpdateReminder: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reminders.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No reminders found. Add by clicking + below.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier) {
            items(
                reminders,
                key = { rem -> rem.id }
            ) { rem ->
                ReminderItem(
                    reminder = rem,
                    onCheckedChange = { onCheckedChange(rem.id) },
                    onSnooze = { newDueTime -> onSnoozeReminder(rem.id, newDueTime) },
                    onDelete = { onDeleteReminder(rem.id) },
                    onUpdate = { onUpdateReminder(rem.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun RemindersWidgetPreview() { // Renamed for clarity
    ReminderMateMockTheme {
        RemindersWidget(
            reminders = SampleData(5),
            onCheckedChange = {},
            onSnoozeReminder = { id, newTime -> println("Snooze $id to $newTime") },
            onDeleteReminder = { id -> println("Delete $id") },
            onUpdateReminder = { id -> println("Update $id") }
        )
    }
}
