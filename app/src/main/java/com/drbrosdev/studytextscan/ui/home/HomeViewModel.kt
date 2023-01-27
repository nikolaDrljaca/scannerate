package com.drbrosdev.studytextscan.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.studytextscan.datastore.AppPreferences
import com.drbrosdev.studytextscan.persistence.entity.FilteredTextModel
import com.drbrosdev.studytextscan.persistence.entity.Scan
import com.drbrosdev.studytextscan.persistence.repository.FilteredTextRepository
import com.drbrosdev.studytextscan.persistence.repository.ScanRepository
import com.drbrosdev.studytextscan.service.entityextraction.EntityExtractionUseCase
import com.drbrosdev.studytextscan.service.entityextraction.ExtractionResultModel
import com.drbrosdev.studytextscan.service.textextract.ScanTextFromImageUseCase
import com.drbrosdev.studytextscan.util.getCurrentDateTime
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: AppPreferences,
    private val scanRepo: ScanRepository,
    private val filteredTextModelRepo: FilteredTextRepository,
    private val scanTextFromImageUseCase: ScanTextFromImageUseCase,
    private val entityExtractionUseCase: EntityExtractionUseCase
): ViewModel() {
    private val _events = Channel<HomeEvents>(capacity = 1)
    val events = _events.receiveAsFlow()

    private val isLoading = MutableStateFlow(true)
    private var homeFragActive = true

    private val listOfScans = scanRepo.allScans
        .distinctUntilChanged()
        .onEach { isLoading.value = false }

    private val listOfPinnedScans = scanRepo.allPinnedScans
        .distinctUntilChanged()
        .onEach { isLoading.value = false }

    private val supportCount = prefs.scanCount
        .onEach {
            val hasSeen = prefs.isFirstLaunch.first()
            if (it % 12 == 0 && hasSeen) {
                _events.send(HomeEvents.ShowSupportDialog)
                prefs.incrementSupportCount()
            }
        }
        .launchIn(viewModelScope)

    val showReward = prefs.showReward

    val state = combine(
        isLoading,
        listOfScans,
        listOfPinnedScans
    ) { loading, scans, pinnedScans ->
        HomeUiState(
            isLoading = loading,
            scans = scans,
            pinnedScans = pinnedScans
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState())

    init {
        initOnBoarding()
    }

    fun handlePermissionDenied() = viewModelScope.launch {
        _events.send(HomeEvents.ShowPermissionInfo)
    }

    private fun createScan(text: String, extractionResultModels: List<ExtractionResultModel>) = viewModelScope.launch {
        if (text.isNotEmpty() or text.isNotBlank()) {
            val scan = Scan(
                scanText = text,
                dateCreated = getCurrentDateTime(),
                dateModified = getCurrentDateTime(),
                scanTitle = "",
                isPinned = false
            )

            val result = scanRepo.insertScan(scan)
            val scanId = result.toInt()

            extractionResultModels.forEach { extractedModel ->
                val model = FilteredTextModel(
                    scanId = scanId,
                    type = extractedModel.type.name.lowercase(),
                    content = extractedModel.content
                )
                filteredTextModelRepo.insertModel(model)
                Log.d("DEBUGn", "createScan: model inserted ${model.content}")
            }

            if (homeFragActive) {
                _events.send(HomeEvents.ShowCurrentScanSaved(scanId)).also {
                    prefs.incrementSupportCount()
                }
            }
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

    private fun initOnBoarding() = viewModelScope.launch {
        val hasSeen = prefs.isFirstLaunch.first()
        if (!hasSeen) {
            _events.send(HomeEvents.ShowOnboarding).also {
                prefs.incrementSupportCount()
            }
        }
    }

    fun handleScan(image: InputImage) {
        showLoadingDialog()
        viewModelScope.launch {
            val completeTextResult = scanTextFromImageUseCase(image)
            completeTextResult.onSuccess { completeText ->
                val extractedEntitiesResult = entityExtractionUseCase(completeText)
                    .fold(
                        onSuccess = { it },
                        onFailure = { emptyList() }
                    )
                createScan(completeText, extractedEntitiesResult)
            }
            completeTextResult.onFailure {
                Log.e("DEBUGn", "Error: ${it.localizedMessage}")
                _events.send(HomeEvents.ShowErrorWhenScanning)
            }
        }
    }

    fun showReward() {
        viewModelScope.launch {
            prefs.showReward()
        }
    }

    fun rewardShown() {
        viewModelScope.launch {
            prefs.rewardShown()
        }
    }

    fun onHomeFrag() { homeFragActive = true }

    fun moveAwayFromScreen() {
        homeFragActive = false
    }
}