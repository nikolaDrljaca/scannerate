package com.drbrosdev.studytextscan.ui.detailscan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.entity.toExtractionModel
import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailScanViewModel(
    val savedStateHandle: SavedStateHandle,
    private val scanRepository: ScanRepository,
    private val filteredModelsRepository: FilteredTextRepository
) : ViewModel() {
    private val args = DetailScanFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val loading = MutableStateFlow(true)

    private val scan = scanRepository.getScanById(args.scanId)

    private val filteredModels = scan
        .flatMapLatest { filteredModelsRepository.getModelsByScanId(it.scanId.toInt()) }
        .map { it.map { filteredTextModel -> filteredTextModel.toExtractionModel() } }
        .onEach { loading.value = false }

    val state = combine(
        loading,
        scan,
        filteredModels
    ) { isLoading, scan, textModels ->
        DetailScanUiState(
            isLoading = isLoading,
            scan = scan,
            filteredTextModels = textModels,
            isCreated = args.isCreated
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailScanUiState())

    private val scanTitle = savedStateHandle.getStateFlow("scan_title", state.value.scan?.scanTitle)
    private val scanContent = savedStateHandle.getStateFlow("scan_content", state.value.scan?.scanText)

    private val updateTitleJob = scanTitle
        .debounce(200)
        .filterNotNull()
        .onEach { title ->
            val updatedScan = state.value.scan?.copy(
                scanTitle = title,
                dateModified = getCurrentDateTime()
            )
            updatedScan?.let { scanRepository.updateScan(it) }
        }
        .launchIn(viewModelScope)

    private val updateContentJob = scanContent
        .debounce(200)
        .filterNotNull()
        .onEach { content ->
            val updatedScan = state.value.scan?.copy(
                scanText = content,
                dateModified = getCurrentDateTime()
            )
            updatedScan?.let { scanRepository.updateScan(it) }
        }
        .launchIn(viewModelScope)

    fun onTitleChange(newValue: String) = savedStateHandle.set("scan_title", newValue)
    fun onContentChanged(newValue: String) = savedStateHandle.set("scan_content", newValue)

    fun deleteScan() = viewModelScope.launch {
        val current = state.value.scan
        current?.let { scanRepository.deleteScan(it) }
    }

    fun updateScanPinned() = viewModelScope.launch {
        state.value.scan?.let {
            val updated = it.copy(isPinned = !it.isPinned)
            scanRepository.updateScan(updated)
        }
    }
}