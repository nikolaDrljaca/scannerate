package com.drbrosdev.studytextscan.ui.detailscan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class DetailScanViewModel(
    savedStateHandle: SavedStateHandle,
    //private val repo: ScanRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(DetailScanState())
    val viewState: StateFlow<DetailScanState> = _viewState

    private val _events = Channel<DetailScanEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun scanUtteranceId(): String {
        return _viewState.value.scan()?.scanId.toString()
    }

}