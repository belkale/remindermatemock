package com.example.remindermatemock.widget

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.example.remindermatemock.model.Reminder
import com.example.remindermatemock.model.SampleData
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme

@Composable
fun RemindersWidget(reminders: List<Reminder>,
                    onCheckedChange: (Int) -> Unit,
                    modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(reminders) { rem ->
            ReminderItem(rem, {onCheckedChange(rem.id) })
        }
    }
}

@Composable
fun ReminderItem(rem: Reminder, onCheckedChange: () -> Unit) {
    val textStyle = if (rem.isCompleted) {
        // Apply strikethrough effect when completed
        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
    } else {
        MaterialTheme.typography.bodyLarge
    }
    ListItem(
        leadingContent = { Checkbox(checked = rem.isCompleted, onCheckedChange = {
            onCheckedChange()
        })},
        headlineContent = { Text(rem.name, style = textStyle) },
        supportingContent = if (rem.description.isEmpty()) null else ({ Text(rem.description) }),
        trailingContent = { Text(rem.due.time.toString()) }
    )
}

@Preview
@Composable
fun RemindersWidgetPreview() {
    ReminderMateMockTheme {
        RemindersWidget(SampleData(), {})
    }
}