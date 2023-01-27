package com.drbrosdev.studytextscan.ui.pdfDialog.components

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PdfDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<String>,
    label: String,
    onSelected: (String) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var item by remember {
        mutableStateOf(items[0])
    }

    ExposedDropdownMenuBox(
        modifier = Modifier.then(modifier),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = item,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colors.primaryVariant,
                unfocusedLabelColor = MaterialTheme.colors.onSurface,
                focusedBorderColor = MaterialTheme.colors.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.onSurface,
                focusedTrailingIconColor = MaterialTheme.colors.primaryVariant
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            items.forEach { selectedItem ->
                DropdownMenuItem(
                    onClick = {
                        item = selectedItem
                        expanded = false
                        onSelected(selectedItem)
                    }
                ) {
                    Text(text = selectedItem)
                }
            }
        }
    }
}