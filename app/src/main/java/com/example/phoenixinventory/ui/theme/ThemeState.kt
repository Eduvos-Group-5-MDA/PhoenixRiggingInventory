package com.example.phoenixinventory.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global theme state manager.
 * Tracks whether the app is in dark or light mode (default: dark mode).
 */
object ThemeState {
    var isDarkMode by mutableStateOf(true)
}
