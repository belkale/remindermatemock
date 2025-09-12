package com.example.remindermatemock.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.remindermatemock.model.RecurringRemindersViewModel
import com.example.remindermatemock.model.ReminderEvent
import com.example.remindermatemock.widget.IntervalUnitFilterDropDown
import com.example.remindermatemock.widget.RecurringReminderItem
import com.example.remindermatemock.widget.RecurringRemindersWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRemindersScreen(
    navController: NavController,
    viewModel: RecurringRemindersViewModel = viewModel()
) {
    // Collect state from the ViewModel. The UI will automatically recompose
    // when these state objects change.
    val recurringReminders by viewModel.filteredRecurringReminders.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Reminders") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Use NavController to go back
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // The filter dropdown menu
            IntervalUnitFilterDropDown(
                selectedFilter = selectedFilter,
                onFilterSelected = { viewModel.onFilterChanged(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecurringRemindersWidget(recurringReminders,
                { id -> viewModel.onEvent(ReminderEvent.DeleteRecurringReminder(id))},
                { rec -> viewModel.onEvent(ReminderEvent.AddRecurringReminder(rec))})
        }
    }
}

