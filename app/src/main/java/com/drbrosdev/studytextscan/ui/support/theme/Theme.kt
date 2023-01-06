package com.drbrosdev.studytextscan.ui.support.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = HeavyBlue,
    primaryVariant = TextColorLight,
    secondary = Color.White,
    secondaryVariant = MidBlue,
    onPrimary = LightBlue,
    background = BackgroundBlue,
    onSecondary = MidBlue,
    onSurface = LightBlue,
    onBackground = LightBlue
)

private val LightColorPalette = lightColors(
    primary = LightBlue,
    primaryVariant = TextColorDark,
    secondary = Color.Black,
    secondaryVariant = Color.Transparent,
    onPrimary = HeavyBlue,
    background = Color.White,
    onSecondary = HeavyBlue,
    onSurface = DarkTextGray,
    onBackground = HeavyBlue

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ScannerateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}