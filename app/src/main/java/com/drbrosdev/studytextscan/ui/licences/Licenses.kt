package com.drbrosdev.studytextscan.ui.licences

val allLicenceItems = listOf(
    LicenceListItem.KoinLicence,
    LicenceListItem.LottieLicence,
    LicenceListItem.EpoxyLicence,
    LicenceListItem.OnBoardingAnim2Screen,
    LicenceListItem.OnBoardingAnim3Screen,
    LicenceListItem.PermissionInfoAnim
)

sealed class LicenceListItem(
    val order: Int,
    val title: String,
    val link: String
) {
    object KoinLicence : LicenceListItem(1, "Koin", "https://insert-koin.io/")
    object LottieLicence : LicenceListItem(2, "Lottie", "https://github.com/airbnb/lottie-android")
    object EpoxyLicence : LicenceListItem(3, "Epoxy", "https://github.com/airbnb/epoxy")
    object OnBoardingAnim2Screen :
        LicenceListItem(4, "Animation 1", "https://lottiefiles.com/57966-edit-animation")

    object OnBoardingAnim3Screen :
        LicenceListItem(5, "Animation 2", "https://lottiefiles.com/47226-pdf-file")

    object PermissionInfoAnim : LicenceListItem(
        order = 7,
        "Animation 3",
        "https://lottiefiles.com/96057-tta-information-session"
    )
}