package com.drbrosdev.studytextscan.ui.detailscan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.entity.toExtractionModel
import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailScanViewModel(
    val savedStateHandle: SavedStateHandle,
    private val scanRepository: ScanRepository,
    private val filteredModelsRepository: FilteredTextRepository
) : ViewModel() {
    private val args = DetailScanFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _events = Channel<DetailScanEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val loading = MutableStateFlow(true)
    private val toShowKeyboard = MutableStateFlow(true)

    private val scan = scanRepository.getScanById(args.scanId)
        .onEach {
            loading.value = false
            delay(100)
            if (args.isCreated == 1 && toShowKeyboard.value) _events.send(DetailScanEvents.ShowSoftwareKeyboardOnFirstLoad)
            toShowKeyboard.value = false
        }

    private val filteredModels = scan
        .flatMapLatest { filteredModelsRepository.getModelsByScanId(it.scanId.toInt()) }
        .map { it.map { filteredTextModel -> filteredTextModel.toExtractionModel() } }

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

    private val scanTitle = MutableStateFlow(state.value.scan?.scanTitle)
    private val scanContent = MutableStateFlow(state.value.scan?.scanText)

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

    fun onTitleChange(newValue: String) = scanTitle.update { newValue }
    fun onContentChanged(newValue: String) = scanContent.update { newValue }

    fun deleteScan() = viewModelScope.launch {
        val current = state.value.scan
        current?.let { scanRepository.deleteScan(it) }
    }

    fun onNavigateUp(title: String, content: String) {
        state.value.scan?.let {
            if (it.scanTitle != title || it.scanText != content)
                viewModelScope.launch {
                    _events.send(DetailScanEvents.ShowUnsavedChanges)
                }
            else
                viewModelScope.launch {
                    _events.send(DetailScanEvents.NavigateUp)
                }
        }
    }

    fun updateScan(title: String, content: String) {
        viewModelScope.launch {
            state.value.scan?.let { scan ->
                val updated = scan.copy(
                    scanTitle = title,
                    scanText = content,
                    dateModified = getCurrentDateTime()
                )
                scanRepository.updateScan(updated)
                _events.send(DetailScanEvents.ShowScanUpdated)
                return@launch
            }
        }
    }

    fun updateScanPinned() = viewModelScope.launch {
        state.value.scan?.let {
            val updated = it.copy(isPinned = !it.isPinned)
            scanRepository.updateScan(updated)
        }
    }
}