package com.example.remindermatemock.widget

// ReminderFormFields.kt (place these in the same file or a new one)

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.remindermatemock.formatKotlinxDateTime
import com.example.remindermatemock.model.IntervalUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// --- Title Field ---
@Composable
fun TitleField(value: String, onValueChange: (String) -> Unit, isError: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Title*") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        supportingText = { if (isError) Text("Title cannot be empty") }
    )
}

// --- Description Field ---
@Composable
fun DescriptionField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3
    )
}

// --- Date & Time Picker Field ---
@OptIn(ExperimentalTime::class)
@Composable
fun DateTimePickerField(
    label: String,
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    isError: Boolean = false
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // Use our helper to format the date for display
    val displayText = formatKotlinxDateTime(selectedDateTime)

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier.clickable { showDatePicker = true }
            )
        },
        isError = isError,
        supportingText = { if (isError) Text("This field is required") }
    )

    if (showDatePicker) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // The month from DatePickerDialog is 0-indexed, so add 1
                selectedDate = LocalDate(year, month + 1, dayOfMonth)
                showDatePicker = false
                showTimePicker = true // Chain to show time picker
            },
            selectedDateTime?.year ?: now.year,
            // The month for DatePickerDialog needs to be 0-indexed, so subtract 1
            selectedDateTime?.month?.number?.minus(1) ?: (now.month.number - 1),
            selectedDateTime?.day ?: now.day
        )
        datePickerDialog.setOnCancelListener { showDatePicker = false }
        datePickerDialog.show()
    }

    if (showTimePicker) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedDate?.let {
                    val newDateTime = LocalDateTime(it, LocalTime(hourOfDay, minute))
                    onDateTimeSelected(newDateTime)
                }
                showTimePicker = false
            },
            selectedDateTime?.hour ?: now.hour,
            selectedDateTime?.minute ?: now.minute,
            true // 24-hour format
        )
        timePickerDialog.setOnCancelListener { showTimePicker = false }
        timePickerDialog.show()
    }
}

// --- Optional End Time Section ---
@Composable
fun OptionalEndTime(
    isEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Set End Time", modifier = Modifier.weight(1f))
            Switch(checked = isEnabled, onCheckedChange = onEnabledChange)
        }
        if (isEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            DateTimePickerField(
                label = "End Time",
                selectedDateTime = selectedDateTime,
                onDateTimeSelected = onDateTimeSelected
            )
        }
    }
}

// --- Repeat Interval Field (updated for IntervalUnit enum) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatIntervalField(
    repeatValue: String,
    onRepeatValueChange: (String) -> Unit,
    intervalUnit: IntervalUnit,
    onIntervalUnitChange: (IntervalUnit) -> Unit
) {
    val isRepeatValueEnabled = intervalUnit != IntervalUnit.NONE

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = repeatValue,
            onValueChange = onRepeatValueChange,
            label = { Text("Every") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = isRepeatValueEnabled
        )

        var isDropdownExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            modifier = Modifier.weight(2f)
        ) {
            OutlinedTextField(
                value = intervalUnit.name.replaceFirstChar { it.titlecase() },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                IntervalUnit.entries.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.name.replaceFirstChar { it.titlecase() }) },
                        onClick = {
                            onIntervalUnitChange(unit)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}