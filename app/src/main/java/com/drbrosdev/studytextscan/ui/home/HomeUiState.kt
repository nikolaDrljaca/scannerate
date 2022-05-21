package com.drbrosdev.studytextscan.ui.home

import com.drbrosdev.studytextscan.persistence.entity.Scan

data class HomeUiState(
    val isLoading: Boolean = false,
    val scans: List<Scan> = emptyList(),
    val pinnedScans: List<Scan> = emptyList()
) {
    val isEmpty = scans.isEmpty() and pinnedScans.isEmpty()
    val itemCount = scans.size + pinnedScans.size
}