package com.drbrosdev.studytextscan.ui.detailscan

import com.drbrosdev.studytextscan.persistence.entity.FilteredTextModel
import com.drbrosdev.studytextscan.persistence.entity.Scan

data class DetailScanUiState(
    val scan: Scan? = null,
    val filteredTextModels: List<FilteredTextModel> = emptyList(),
    val isLoading: Boolean = true,
)
