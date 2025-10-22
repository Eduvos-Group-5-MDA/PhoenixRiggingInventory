package com.example.phoenixinventory.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Dark color scheme configuration for Material 3.
 * Uses custom Phoenix Rigging dark theme colors.
 */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkCharcoal,
    tertiary = DarkPrimaryContainer,
    background = DarkCarbon,
    surface = DarkCardDark,
    onPrimary = DarkOnDark,
    onSecondary = DarkOnDark,
    onTertiary = DarkOnDark,
    onBackground = DarkOnDark,
    onSurface = DarkOnDark
)

/**
 * Light color scheme configuration for Material 3.
 * Optional alternative theme (app primarily uses dark theme).
 */
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightCard,
    tertiary = LightPrimaryContainer,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnLight,
    onSecondary = LightOnLight,
    onTertiary = LightOnLight,
    onBackground = LightOnLight,
    onSurface = LightOnLight
)

/**
 * Main theme composable for Phoenix Rigging Inventory app.
 * Supports both dark and light themes with custom color schemes.
 */
@Composable
fun PhoenixInventoryTheme(
    darkTheme: Boolean = ThemeState.isDarkMode,
    // Dynamic color disabled to use custom colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}