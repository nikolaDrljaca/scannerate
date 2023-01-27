package com.drbrosdev.studytextscan.ui.detailscan

import com.drbrosdev.studytextscan.persistence.entity.ExtractionModel
import com.drbrosdev.studytextscan.persistence.entity.Scan

data class DetailScanUiState(
    val scan: Scan? = null,
    val filteredTextModels: List<ExtractionModel> = emptyList(),
    val isLoading: Boolean = true,
    val isCreated: Int = 0
)
