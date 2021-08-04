package com.drbrosdev.studytextscan.ui.home

sealed class HomeEvents {
    object ShowLoadingDialog: HomeEvents()
    data class ShowCurrentScanSaved(val id: Int): HomeEvents()
    object ShowScanEmpty: HomeEvents()
}