package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun ScanDetailHeader(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChanged: (String) -> Unit,
    dateCreated: String,
    dateModified: String,
    isCreated: Int
) {
    var titleText by remember { mutableStateOf(title) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = isCreated) {
        if (isCreated == 1) focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.then(modifier)
    ) {
        ScannerateTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            text = titleText,
            onTextChanged = {
                titleText = it
                onTitleChanged(it)
            },
            maxLines = 2
        )
        ScannerateDateText(
            text = dateCreated
        )
        ScannerateDateText(
            text = dateModified
        )
    }
}