package com.drbrosdev.studytextscan.ui.pdfDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.pdfDialog.components.PdfDropdownMenu

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PdfDialog(
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {
            /*TODO*/
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Row(
            modifier = Modifier
                .then(modifier),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {

        }
        PdfDropdownMenu(
            listOf(
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "20",
                "22",
                "24",
                "26",
                "28",
                "30",
                "32",
                "36",
                "40",
                "44"
            ),
            modifier = Modifier.padding(
                start = 10.dp,
                top = 12.dp
            )
        )

        PdfDropdownMenu(
            listOf(
                stringResource(R.string.black_color),
                stringResource(R.string.blue_color),
                stringResource(R.string.red_color),
                stringResource(R.string.green_color),
                stringResource(R.string.yellow_color)
            ),
            modifier = Modifier.padding(
                start = 10.dp,
                top = 12.dp
            )
        )
    }
}