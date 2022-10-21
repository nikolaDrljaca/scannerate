package com.drbrosdev.studytextscan.ui.home

import com.drbrosdev.studytextscan.persistence.entity.Scan

sealed class HomeEvents {
    object ShowLoadingDialog: HomeEvents()
    data class ShowCurrentScanSaved(val id: Int): HomeEvents()
    object ShowScanEmpty: HomeEvents()
    data class ShowUndoDeleteScan(val scan: Scan): HomeEvents()
    object ShowOnboarding: HomeEvents()
    object ShowErrorWhenScanning: HomeEvents()
    object ShowPermissionInfo: HomeEvents()
    object ShowSupportDialog: HomeEvents()
}