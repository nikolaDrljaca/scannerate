package com.drbrosdev.studytextscan.ui.home.reward

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import kotlinx.coroutines.delay

fun FragmentScanHomeBinding.setupComposeSnackbar() {
    composeViewSnackBar.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ScannerateTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(2000)
                        snackbarHostState.showSnackbar("")
                    }
                }
                RewardToast(snackbarHostState = snackbarHostState)
            }
        }
    }
}
