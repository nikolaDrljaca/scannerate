package com.drbrosdev.studytextscan.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.datastore.AppPreferences
import com.drbrosdev.studytextscan.persistence.entity.FilteredTextModel
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.util.Resource
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import com.drbrosdev.studytextscan.util.setState
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: AppPreferences,
    private val scanRepo: ScanRepository,
    private val filteredTextModelRepo: FilteredTextRepository,
    private val scanTextFromImageUseCase: ScanTextFromImageUseCase
): ViewModel() {
    private val _viewState = MutableStateFlow(HomeState())
    val viewState: StateFlow<HomeState> = _viewState

    private val _events = Channel<HomeEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        getScans()
        initOnboarding()
    }

    private fun createScan(text: String, filteredTextList: List<Pair<String, String>>) = viewModelScope.launch {
        if (text.isNotEmpty() or text.isNotBlank()) {
            val scan = Scan(
                scanText = text,
                dateCreated = getCurrentDateTime(),
                dateModified = getCurrentDateTime(),
                scanTitle = "",
                isPinned = false
            )

            val result = scanRepo.insertScan(scan)
            val scanId = Integer.parseInt(result.toString())

            filteredTextList.forEach {
                val model = FilteredTextModel(scanId = scanId, type = it.first, content = it.second)
                /*
                Now this needs to be inserted into the database.
                 */
                filteredTextModelRepo.insertModel(model)
                Log.d("DEBUGn", "createScan: model inserted ${model.content}")
            }

            _events.send(HomeEvents.ShowCurrentScanSaved(scanId))
        } else {
            _events.send(HomeEvents.ShowScanEmpty)
        }

    }

    private fun showLoadingDialog() = viewModelScope.launch {
        _events.send(HomeEvents.ShowLoadingDialog)
    }

    fun deleteScan(scan: Scan) = viewModelScope.launch {
        scanRepo.deleteScan(scan)
        _events.send(HomeEvents.ShowUndoDeleteScan(scan))
    }

    fun insertScan(scan: Scan) = viewModelScope.launch {
        scanRepo.insertScan(scan)
    }

    private fun initOnboarding() = viewModelScope.launch {
        val hasSeen = prefs.isFirstLaunch.first()
        if (!hasSeen) {
            _events.send(HomeEvents.ShowOnboarding)
            prefs.firstLaunchComplete()
        }
    }

    private fun getScans() = viewModelScope.launch {
        try {
            scanRepo.getAllScans().collect {
                Log.d("DEBUGn", "getScans: ${it.size}")
                _viewState.setState { copy(scanList = Resource.Success(it)) }
            }
        } catch (e: Exception) {
            Log.e("DEBUGn", "getScans: ", e)
            _viewState.setState { copy(scanList = Resource.Error(e)) }
        }
    }

    fun handleScan(image: InputImage) {
        showLoadingDialog()
        viewModelScope.launch {
            //TODO: error check, wrap in try catch block
            try {
                val (completeText, filteredText) = scanTextFromImageUseCase(image)
                createScan(completeText, filteredText)
            } catch (e: Exception) {
                Log.e("DEBUGn", "Error: ${e.localizedMessage}")
                _events.send(HomeEvents.ShowErrorWhenScanning)
            }
        }
    }
}