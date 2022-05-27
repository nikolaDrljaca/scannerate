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
            LicenceListItem.EpoxyLicence,
            LicenceListItem.OnBoardingAnim2Screen,
            LicenceListItem.OnBoardingAnim3Screen,
            LicenceListItem.AppIcon,
            LicenceListItem.PermissionInfoAnim
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
        object OnBoardingAnim2Screen : LicenceListItem(4, "Animation 1", "https://lottiefiles.com/57966-edit-animation")
        object OnBoardingAnim3Screen : LicenceListItem(5,"Animation 2", "https://lottiefiles.com/47226-pdf-file")
        object AppIcon : LicenceListItem(6, "App Icon", "https://www.flaticon.com/free-icon/text-recognising_3997552?related_id=3997610&origin=search")
        object PermissionInfoAnim: LicenceListItem(order = 7, "Animation 3", "https://lottiefiles.com/96057-tta-information-session")
    }
}