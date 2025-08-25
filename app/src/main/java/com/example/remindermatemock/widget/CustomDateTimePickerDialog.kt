package com.example.remindermatemock.widget

import kotlin.time.ExperimentalTime
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * A composable that manages the display of a DatePickerDialog followed by a TimePickerDialog
 * to select a custom LocalDateTime.
 *
 * @param showDialog Controls the visibility of the picker flow. Set to true to show.
 * @param initialDateTime The initial date and time to be shown in the pickers. Defaults to now.
 * @param onDateTimeSelected Callback function that is invoked when a date and time have been successfully selected.
 *                           The selected LocalDateTime is guaranteed to be in the future.
 * @param onDismiss Callback function that is invoked when the dialog flow is dismissed (either by cancellation or selection).
 */
@OptIn(ExperimentalTime::class)
@Composable
fun CustomDateTimePickerDialog(
    showDialog: Boolean,
    initialDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var showDatePickerState by remember { mutableStateOf(false) }
    var showTimePickerState by remember { mutableStateOf(false) }
    var selectedDateState by remember { mutableStateOf<LocalDate?>(null) }

    // This LaunchedEffect will trigger the dialog flow when showDialog becomes true
    LaunchedEffect(showDialog) {
        if (showDialog) {
            showDatePickerState = true
        }
    }

    if (showDatePickerState) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> // Month from DatePickerDialog is 0-indexed
                selectedDateState = LocalDate(year, month + 1, dayOfMonth)
                showDatePickerState = false
                showTimePickerState = true // Chain to show time picker
            },
            initialDateTime.year,
            initialDateTime.month.number - 1, // Month for DatePickerDialog is 0-indexed
            initialDateTime.day
        ).apply {
            setOnCancelListener {
                showDatePickerState = false
                onDismiss() // Notify dismissal
            }
            // Optionally set min date to prevent selection of past dates in the date picker itself
            datePicker.minDate = Clock.System.now().toEpochMilliseconds() // Be careful with timezones here
            show()
        }
    }

    if (showTimePickerState) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                selectedDateState?.let { date ->
                    val newSelectedDateTime = LocalDateTime(date, LocalTime(hourOfDay, minute))

                    // Ensure selected time is in the future
                    if (newSelectedDateTime.toInstant(TimeZone.currentSystemDefault()) > kotlin.time.Clock.System.now()) {
                        onDateTimeSelected(newSelectedDateTime)
                    } else {
                        Toast.makeText(context, "Cannot select a time in the past", Toast.LENGTH_SHORT).show()
                        // Optionally, you could re-show the time picker or date picker here
                        // or just let it dismiss. For simplicity, we'll dismiss.
                    }
                }
                showTimePickerState = false
                selectedDateState = null // Reset for next use
                onDismiss() // Notify dismissal after selection or error
            },
            initialDateTime.hour, // Use initialDateTime for consistency
            initialDateTime.minute,
            true // 24-hour format
        ).apply {
            setOnCancelListener {
                showTimePickerState = false
                selectedDateState = null // Reset if cancelled
                onDismiss() // Notify dismissal
            }
            show()
        }
    }
}
