package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val WorkforceDarkColorScheme = darkColorScheme(
    primary = WorkforceRed,
    onPrimary = WorkforceTextPrimary,
    primaryContainer = WorkforceRedDark,
    onPrimaryContainer = WorkforceTextPrimary,
    secondary = WorkforceOrange,
    onSecondary = WorkforceTextPrimary,
    secondaryContainer = WorkforceOrangeLight,
    onSecondaryContainer = WorkforceTextPrimary,
    tertiary = ComplianceGreen,
    background = WorkforceDarkBg,
    onBackground = WorkforceTextPrimary,
    surface = WorkforceSurface,
    onSurface = WorkforceTextPrimary,
    surfaceVariant = WorkforceSurfaceVariant,
    onSurfaceVariant = WorkforceTextSecondary,
    outline = WorkforceCardBorder,
    error = ComplianceRed,
    onError = WorkforceTextPrimary
)

private val WorkforceLightColorScheme = lightColorScheme(
    primary = WorkforceRed,
    onPrimary = WorkforceTextPrimary,
    primaryContainer = WorkforceRedLight,
    onPrimaryContainer = WorkforceRedDark,
    secondary = WorkforceOrange,
    onSecondary = WorkforceTextPrimary,
    tertiary = ComplianceGreen,
    background = WorkforceDarkBg, // Keep dark professional industrial look
    onBackground = WorkforceTextPrimary,
    surface = WorkforceSurface,
    onSurface = WorkforceTextPrimary,
    surfaceVariant = WorkforceSurfaceVariant,
    onSurfaceVariant = WorkforceTextSecondary,
    outline = WorkforceCardBorder,
    error = ComplianceRed,
    onError = WorkforceTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to professional dark industrial theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WorkforceDarkColorScheme else WorkforceLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = WorkforceDarkBg.toArgb()
                window.navigationBarColor = WorkforceDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
