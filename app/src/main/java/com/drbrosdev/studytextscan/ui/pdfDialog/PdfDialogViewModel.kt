package com.drbrosdev.studytextscan.ui.pdfDialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PdfDialogViewModel(
    val savedStateHandle: SavedStateHandle,
    private val scanRepo: ScanRepository
) : ViewModel() {

    private val scanId = savedStateHandle.get<Int>("pdf_scan_id") ?: 0

    fun getScan(action: (Scan?) -> Unit) {
        viewModelScope.launch {
            scanRepo.getScanById(scanId).collect {
                action(it())
            }
        }
    }
}