package com.drbrosdev.studytextscan.ui.support.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.drbrosdev.studytextscan.R

@OptIn(ExperimentalTextApi::class)
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
private val RubikFontName = GoogleFont(name = "Rubik")

@OptIn(ExperimentalTextApi::class)
private val Rubik = FontFamily(Font(googleFont = RubikFontName, fontProvider = provider))

val Typography = Typography(
    defaultFontFamily = Rubik
)