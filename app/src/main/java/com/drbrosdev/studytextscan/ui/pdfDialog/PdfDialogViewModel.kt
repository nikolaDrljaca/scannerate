package com.drbrosdev.studytextscan.ui.pdfDialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PdfDialogViewModel(
    val savedStateHandle: SavedStateHandle,
    private val scanRepo: ScanRepository
) : ViewModel() {
    private val args = PdfDialogFragmentArgs.fromSavedStateHandle(savedStateHandle)

    val scan = scanRepo.getScanById(args.pdfScanId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

}