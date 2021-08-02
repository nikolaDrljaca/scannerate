package com.drbrosdev.studytextscan.ui.detailscan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import com.drbrosdev.studytextscan.util.setState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailScanViewModel(
    val savedStateHandle: SavedStateHandle,
    private val repo: ScanRepository
) : ViewModel() {

    private val scanId = savedStateHandle.get<Int>("scan_id") ?: 0
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
        current?.let { repo.deleteScan(it) }
    }

    fun updateScan(title: String, content: String) {
        viewModelScope.launch {
            _viewState.value.scan()?.let { scan ->
                val updated = scan.copy(scanTitle = title, scanText = content, dateModified = getCurrentDateTime())
                repo.updateScan(updated)
                _events.send(DetailScanEvents.ShowScanUpdated)
                return@launch
            }
            //maybe send event if something fails?
        }
    }

    private fun initializeScan() = viewModelScope.launch {
        repo.getScanById(scanId).collect {
            _viewState.setState { copy(scan = it) }
            /*
            delay allows views to instantiate so focusing can work
            This works, later possibly look for a 'more elegant' solution.
             */
            delay(100)
            if (isJustCreated == 1) _events.send(DetailScanEvents.ShowSoftwareKeyboardOnFirstLoad)
        }
    }
}