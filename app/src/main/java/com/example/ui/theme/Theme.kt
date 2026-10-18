package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AutoVerseDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = GlassTextPrimary,
    primaryContainer = GlassNavActivePill,
    onPrimaryContainer = ElectricBlueLight,
    secondary = NeonRed,
    onSecondary = GlassTextPrimary,
    tertiary = NeonCyan,
    background = DeepBlack,
    onBackground = GlassTextPrimary,
    surface = DarkSurface,
    onSurface = GlassTextPrimary,
    surfaceVariant = DarkBackground,
    onSurfaceVariant = GlassTextSecondary,
    outline = GlassBorderColor
)

@Composable
fun AutoVerseTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = AutoVerseDarkColorScheme,
        typography = AutoVerseTypography,
        content = content
    )
}
