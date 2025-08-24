package com.example.remindermatemock.widget

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainMenu(expanded: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = { Text("Overdue") },
            onClick = { /* Do something... */ }
        )
        DropdownMenuItem(
            text = { Text("Recurrences") },
            onClick = { /* Do something... */ }
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { /* Do something... */ }
        )
    }
}