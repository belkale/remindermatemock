package com.example.remindermatemock.widget

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HelpDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        title = {
            Text(text = "Help")
        },
        text = {
            Text("Here's how to use the Reminders app:\n\n" +
                    "* Add a new reminder by clicking on + at bottom right.\n" +
                    "* Click on 3 dots on right of a reminder for further actions.\n" +
                    "* Swipe left or right to go to next or previous day.\n" +
                    "* Long press on icons in bottom navigation bar to " +
                        "know their functionality.\n")
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ok")
            }
        },
    )
}