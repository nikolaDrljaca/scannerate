package com.drbrosdev.studytextscan.ui.home.reward

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.drbrosdev.studytextscan.databinding.FragmentScanHomeBinding
import com.drbrosdev.studytextscan.ui.support.theme.ScannerateTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun FragmentScanHomeBinding.setupComposeSnackbar(
    rewardCount: Flow<Boolean>,
    onRewardShown: () -> Unit
) {
    composeViewSnackBar.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            ScannerateTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    rewardCount.collect {
                        delay(1000)
                        if (it) {
                            snackbarHostState.showSnackbar("")
                            onRewardShown()
                        }
                    }
                }
                RewardToast(snackbarHostState = snackbarHostState)
            }
        }
    }
}
