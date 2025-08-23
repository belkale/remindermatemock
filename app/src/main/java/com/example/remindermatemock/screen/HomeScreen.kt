package com.example.remindermatemock.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.remindermatemock.model.ReminderViewModel
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme
import com.example.remindermatemock.widget.RemindersWidget

private const val TAG = "Home"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(reminderViewModel: ReminderViewModel = viewModel()) {
    var presses by remember { mutableIntStateOf(0) }
    var showCompletedToggled by rememberSaveable { mutableStateOf(false) }
    val reminders by reminderViewModel.reminders.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Reminder Mate") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Info, contentDescription = "Help")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround, // This is the key
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Calendar")
                    }
                    Text("Aug 23, 2025")


                    IconButton(onClick = {
                        showCompletedToggled = !showCompletedToggled
                        Log.d(TAG, "showCompletedToggled: $showCompletedToggled")
                    }) {
                        if (showCompletedToggled)
                            Icon(Icons.Filled.Check, contentDescription = "Hide Completed")
                        else
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Show Completed")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { presses++ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        RemindersWidget(
            reminders,
            onCheckedChange = { reminderId ->
                // This is the "event flowing up"
                reminderViewModel.toggleCompletion(reminderId)
            },
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ReminderMateMockTheme {
        HomeScreen()
    }
}