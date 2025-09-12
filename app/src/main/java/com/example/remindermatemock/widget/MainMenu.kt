package com.example.remindermatemock.widget

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.remindermatemock.AppDestinations

@Composable
fun MainMenu(expanded: Boolean, navController: NavController, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = { Text("Overdue") },
            onClick = { navController.navigate(AppDestinations.OVERDUE_ROUTE) }
        )
        DropdownMenuItem(
            text = { Text("Recurrences") },
            onClick = { navController.navigate(AppDestinations.RECURRING_REMINDERS_ROUTE) }
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { /* Do something... */ }
        )
    }
}