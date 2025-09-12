package com.example.remindermatemock.widget

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.example.remindermatemock.model.RecurringReminder
import com.example.remindermatemock.model.SampleRecurringReminders
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme

private const val TAG = "RecurringRemindersWidget"

@Composable
fun RecurringRemindersWidget(
    recurringReminders: List<RecurringReminder>,
    onDelete: (id: Int) -> Unit,
    onUpdate: (rec: RecurringReminder) -> Unit,
    modifier: Modifier = Modifier
) {
    // The list of reminders
    if (recurringReminders.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No reminders found for this filter.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recurringReminders, key = { it.id }) { rr ->
                RecurringReminderItem(recurringReminder = rr,
                    onDelete = { onDelete(rr.id) },
                    onUpdate = onUpdate)
            }
        }
    }
}

@Preview
@Composable
fun RecurringRemindersWidgetPreview(modifier: Modifier = Modifier) {
    ReminderMateMockTheme {
        RecurringRemindersWidget(
            recurringReminders = SampleRecurringReminders(),
            onDelete = { id -> println("Delete $id") },
            onUpdate = { id -> println("Update $id") }
        )
    }
}