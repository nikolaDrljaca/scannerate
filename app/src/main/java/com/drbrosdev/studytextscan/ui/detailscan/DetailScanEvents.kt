package com.drbrosdev.studytextscan.ui.detailscan

sealed class DetailScanEvents {
    object ShowSoftwareKeyboardOnFirstLoad: DetailScanEvents()
    object ShowScanUpdated: DetailScanEvents()
    object ShowUnsavedChanges: DetailScanEvents()
    object NavigateUp: DetailScanEvents()
}