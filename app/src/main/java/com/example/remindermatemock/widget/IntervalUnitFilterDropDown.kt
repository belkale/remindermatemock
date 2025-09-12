package com.example.remindermatemock.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.remindermatemock.model.IntervalUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalUnitFilterDropDown(
    selectedFilter: IntervalUnit?,
    onFilterSelected: (IntervalUnit?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // The list of options includes "All" (represented by null) plus all IntervalUnit values
    val filterOptions = listOf<IntervalUnit?>(null) + IntervalUnit.entries.toTypedArray()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedFilter?.name ?: "All",
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter by Interval") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filterOptions.forEach { filterOption ->
                DropdownMenuItem(
                    text = {
                        Text(text = filterOption?.name ?: "All")
                    },
                    onClick = {
                        onFilterSelected(filterOption)
                        expanded = false
                    }
                )
            }
        }
    }
}