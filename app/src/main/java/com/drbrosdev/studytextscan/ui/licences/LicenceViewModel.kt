package com.drbrosdev.studytextscan.ui.licences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LicenceViewModel() : ViewModel() {

    lateinit var allLicenceItems: List<LicenceListItem>

    init {
        getLicenceListItems()
    }

    private fun getLicenceListItems() = viewModelScope.launch {
        allLicenceItems = listOf(
            LicenceListItem.KoinLicence,
            LicenceListItem.LottieLicence,
            LicenceListItem.EpoxyLicence
        )
    }

    sealed class LicenceListItem(
        val order: Int,
        val title: String,
        val link: String
    ) {
        object KoinLicence : LicenceListItem(1,"Koin", "https://insert-koin.io/")
        object LottieLicence : LicenceListItem(2,"Lottie", "https://github.com/airbnb/lottie-android")
        object EpoxyLicence: LicenceListItem(3,"Epoxy", "https://github.com/airbnb/epoxy")

    }
}