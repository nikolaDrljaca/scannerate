package com.drbrosdev.studytextscan.ui.detailscan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.Resource
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import com.drbrosdev.studytextscan.util.setState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailScanViewModel(
    val savedStateHandle: SavedStateHandle,
    private val scanRepository: ScanRepository,
    private val filteredModelsRepository: FilteredTextRepository
) : ViewModel() {

    val scanId = savedStateHandle.get<Int>("scan_id") ?: 0
    private val isJustCreated = savedStateHandle.get<Int>("is_created") ?: 0

    private val _viewState = MutableStateFlow(DetailScanState())
    val viewState: StateFlow<DetailScanState> = _viewState

    private val _events = Channel<DetailScanEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        initializeScan()
    }

    fun scanUtteranceId(): String {
        return _viewState.value.scan()?.scanId.toString()
    }

    fun deleteScan() = viewModelScope.launch {
        val current = _viewState.value.scan()
        current?.let { scanRepository.deleteScan(it) }
    }

    fun onNavigateUp(title: String, content: String) {
        _viewState.value.scan()?.let {
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
            _viewState.value.scan()?.let { scan ->
                val updated = scan.copy(
                    scanTitle = title,
                    scanText = content,
                    dateModified = getCurrentDateTime()
                )
                scanRepository.updateScan(updated)
                _events.send(DetailScanEvents.ShowScanUpdated)
                initializeScan(false)
                return@launch
            }
            //maybe send event if something fails?
        }
    }

    fun updateScanPinned() = viewModelScope.launch {
        _viewState.value.scan()?.let {
            val updated = it.copy(isPinned = !it.isPinned)
            scanRepository.updateScan(updated)
            initializeScan(false)
        }
    }

    fun getCurrentScan(action: (Scan?) -> Unit){
        action(_viewState.value.scan())
    }

    private fun initializeScan(showKeyboard: Boolean = true) = viewModelScope.launch {
        combine(
            scanRepository.getScanById(scanId),
            filteredModelsRepository.getModelsByScanId(scanId)
        ) { scan, models ->
            _viewState.setState { copy(scan = scan, filteredTextModels = Resource.Success(models)) }
        }.collect {
            delay(100)
            if (isJustCreated == 1 && showKeyboard) _events.send(DetailScanEvents.ShowSoftwareKeyboardOnFirstLoad)
        }
    }
}