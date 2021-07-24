package com.drbrosdev.studytextscan.ui.util

class UserInterfaceUtil {

    companion object{
        private val _allLicenceItems = listOf(
            LicenceListItem.KoinLicence,
            LicenceListItem.CoilLicence
        )

        val allLicenceItems = _allLicenceItems
        sealed class LicenceListItem(
            val title: String,
            val link: String
        ) {
            object KoinLicence: LicenceListItem("Koin","Koin Link")
            object CoilLicence: LicenceListItem("Coil", "CoilLink")
        }
    }

}