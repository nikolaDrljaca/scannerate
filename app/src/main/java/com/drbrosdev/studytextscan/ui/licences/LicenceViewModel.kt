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
            LicenceListItem.CoilLicence
        )
    }

    sealed class LicenceListItem(
        val order: Int,
        val title: String,
        val link: String
    ) {
        object KoinLicence : LicenceListItem(1,"Koin", "Koin Link")
        object CoilLicence : LicenceListItem(2,"Coil", "Coil Link")
    }
}