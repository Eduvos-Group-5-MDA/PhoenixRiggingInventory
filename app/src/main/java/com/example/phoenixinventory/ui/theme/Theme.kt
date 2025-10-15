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