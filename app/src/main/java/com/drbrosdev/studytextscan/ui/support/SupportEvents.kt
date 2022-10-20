package com.drbrosdev.studytextscan.ui.support

sealed interface SupportEvents {
    object NavigateToReward: SupportEvents
    data class ErrorOccured(val message: String): SupportEvents
}