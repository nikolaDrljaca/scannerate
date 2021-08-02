package com.drbrosdev.studytextscan.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.Resource
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import com.drbrosdev.studytextscan.util.setState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: ScanRepository
): ViewModel() {
    private val _viewState = MutableStateFlow(HomeState())
    val viewState: StateFlow<HomeState> = _viewState

    private val _events = Channel<HomeEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        getScans()
    }

    fun createScan(text: String) = viewModelScope.launch {
        val scan = Scan(
            scanText = text,
            dateCreated = getCurrentDateTime(),
            dateModified = getCurrentDateTime(),
            scanTitle = ""
        )

        val result = repo.insertScan(scan)
        val scanId = Integer.parseInt(result.toString())
        _events.send(HomeEvents.ShowCurrentScanSaved(scanId))
    }

    fun showLoadingDialog() = viewModelScope.launch {
        _events.send(HomeEvents.ShowLoadingDialog)
    }

    private fun getScans() = viewModelScope.launch {
        try {
            repo.getAllScans().collect {
                Log.d("DEBUGn", "getScans: ${it.size}")
                _viewState.setState { copy(scanList = Resource.Success(it)) }
            }
        } catch (e: Exception) {
            Log.e("DEBUGn", "getScans: ", e)
            _viewState.setState { copy(scanList = Resource.Error(e)) }
        }
    }
}