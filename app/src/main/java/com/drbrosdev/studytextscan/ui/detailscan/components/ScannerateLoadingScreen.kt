package com.drbrosdev.studytextscan.ui.detailscan.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun ScannerateLoadingScreen(
    modifier: Modifier = Modifier
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentAlignment = Alignment.TopCenter
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(statusBarHeight),
            color = MaterialTheme.colors.onPrimary,
            strokeCap = StrokeCap.Square
        )
    }
}