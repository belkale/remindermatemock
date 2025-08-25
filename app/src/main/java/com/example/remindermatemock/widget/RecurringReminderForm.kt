package com.example.remindermatemock.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindermatemock.model.IntervalUnit
import com.example.remindermatemock.model.Recurrence
import com.example.remindermatemock.model.RecurringReminder
import com.example.remindermatemock.ui.theme.ReminderMateMockTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// This class holds the transient state of the form fields
private data class ReminderFormState(
    val title: String = "",
    val description: String = "",
    val startTime: LocalDateTime? = null,
    val isEndTimeEnabled: Boolean = false,
    val endTime: LocalDateTime? = null,
    val repeatValue: String = "0",
    val intervalUnit: IntervalUnit = IntervalUnit.NONE,

    // Validation flags
    val isTitleError: Boolean = false,
    val isStartTimeError: Boolean = false
)
@Composable
fun RecurringReminderForm(existingReminder: RecurringReminder?,
                             onConfirm: (RecurringReminder) -> Unit,
                             onCancel: () -> Unit,
                             modifier: Modifier = Modifier) {

    // This state is derived from the input and remembered.
    // The key ensures that if `existingReminder` changes, the state is re-initialized.
    var formState by remember(existingReminder) {
        mutableStateOf(
            if (existingReminder != null) {
                // UPDATE MODE: Populate state from existing data
                // For simplicity, this form edits the *first* recurrence rule.
                val firstRecurrence = existingReminder.recurrences.firstOrNull()
                ReminderFormState(
                    title = existingReminder.title,
                    description = existingReminder.description,
                    startTime = firstRecurrence?.startTime,
                    isEndTimeEnabled = firstRecurrence?.endTime != null,
                    endTime = firstRecurrence?.endTime,
                    repeatValue = firstRecurrence?.repeatInterval?.toString() ?: "0",
                    intervalUnit = firstRecurrence?.intervalUnit ?: IntervalUnit.NONE
                )
            } else {
                // CREATE MODE: Start with a default, empty state
                ReminderFormState()
            }
        )
    }

    // --- Validation and Confirmation Logic ---
    val onConfirmClick: () -> Unit = {
        // 1. Determine the validation status
        val isTitleInvalid = formState.title.isBlank()
        val isStartTimeInvalid = formState.startTime == null

        // 2. Update the state to show any errors
        formState = formState.copy(
            isTitleError = isTitleInvalid,
            isStartTimeError = isStartTimeInvalid
        )

        // 3. Check if there are any errors. If not, proceed.
        val hasError = isTitleInvalid || isStartTimeInvalid
        if (!hasError) {
            // All the confirmation logic now goes inside this block
            val recurrence = Recurrence(
                startTime = formState.startTime!!, // Safe now because we've validated
                endTime = if (formState.isEndTimeEnabled) formState.endTime else null,
                repeatInterval = if (formState.intervalUnit != IntervalUnit.NONE) {
                    formState.repeatValue.toIntOrNull() ?: 1
                } else 0,
                intervalUnit = formState.intervalUnit
            )

            val newOrUpdatedReminder = RecurringReminder(
                id = existingReminder?.id ?: 0,
                title = formState.title.trim(),
                description = formState.description.trim(),
                recurrences = listOf(recurrence)
            )

            onConfirm(newOrUpdatedReminder)
        }
        // If there was an error, the lambda simply finishes here without doing anything else.
    }

    // --- UI Layout ---
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (existingReminder == null) "Create Reminder" else "Update Reminder",
            style = MaterialTheme.typography.headlineSmall
        )

        TitleField(
            value = formState.title,
            onValueChange = { formState = formState.copy(title = it, isTitleError = false) },
            isError = formState.isTitleError
        )

        DescriptionField(
            value = formState.description,
            onValueChange = { formState = formState.copy(description = it) }
        )

        DateTimePickerField(
            label = "Start Time*",
            selectedDateTime = formState.startTime,
            onDateTimeSelected = { formState = formState.copy(startTime = it, isStartTimeError = false) },
            isError = formState.isStartTimeError
        )

        OptionalEndTime(
            isEnabled = formState.isEndTimeEnabled,
            onEnabledChange = { isEnabled ->
                formState = formState.copy(
                    isEndTimeEnabled = isEnabled,
                    // Clear end time when disabled
                    endTime = if (!isEnabled) null else formState.endTime
                )
            },
            selectedDateTime = formState.endTime,
            onDateTimeSelected = { formState = formState.copy(endTime = it) }
        )

        RepeatIntervalField(
            repeatValue = formState.repeatValue,
            onRepeatValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    formState = formState.copy(repeatValue = newValue)
                }
            },
            intervalUnit = formState.intervalUnit,
            onIntervalUnitChange = { formState = formState.copy(intervalUnit = it) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- Action Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = onConfirmClick,
                modifier = Modifier.weight(1f),
                enabled = formState.title.isNotBlank() && formState.startTime != null
            ) {
                Text(if (existingReminder == null) "Create" else "Save")
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Create Mode")
@Composable
private fun RecurringReminderFormCreatePreview() {
    ReminderMateMockTheme {
        RecurringReminderForm(
            existingReminder = null,
            onConfirm = { println("Confirmed: $it") },
            onCancel = { println("Cancelled") }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true, name = "Update Mode")
@Composable
private fun RecurringReminderFormUpdatePreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val sampleReminder = RecurringReminder(
        id = 123,
        title = "Team Standup",
        description = "Daily sync meeting",
        recurrences = listOf(
            Recurrence(
                startTime = now.date.atTime(9, 0),
                endTime = now.date.atTime(9, 15),
                repeatInterval = 1,
                intervalUnit = IntervalUnit.DAY
            )
        )
    )
    ReminderMateMockTheme {
        RecurringReminderForm(
            existingReminder = sampleReminder,
            onConfirm = { println("Confirmed Update: $it") },
            onCancel = { println("Cancelled") }
        )
    }
}