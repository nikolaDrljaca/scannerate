package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScannerateLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.TopCenter
    ) {
        LinearProgressIndicator(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            color = MaterialTheme.colors.onPrimary
        )
    }
}