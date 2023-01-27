package com.drbrosdev.studytextscan.ui.pdfDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drbrosdev.studytextscan.R
import com.drbrosdev.studytextscan.ui.pdfDialog.components.PdfDropdownMenu

@Composable
fun PdfDialog(
    modifier: Modifier = Modifier,
    colorList: List<String>,
    fontSizeList: List<String>,
    onCancelClick: () -> Unit,
    onExportClick: (String, String) -> Unit
) {
    val titleTextStyle = TextStyle(
        color = MaterialTheme.colors.primaryVariant,
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium
    )

    var selectedFont by remember {
        mutableStateOf(fontSizeList.first())
    }

    var selectedColor by remember {
        mutableStateOf(colorList.first())
    }

    Surface(
        color = MaterialTheme.colors.background,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.customize_your_export),
                style = titleTextStyle,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            PdfDropdownMenu(
                items = colorList,
                label = stringResource(id = R.string.color),
                onSelected = { selectedColor = it }
            )

            PdfDropdownMenu(
                items = fontSizeList,
                label = stringResource(id = R.string.font_size),
                onSelected = { selectedFont = it }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.primaryVariant)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }

                Button(onClick = { onExportClick(selectedColor, selectedFont) }) {
                    Text(text = stringResource(id = R.string.export))
                }
            }
        }
    }
}