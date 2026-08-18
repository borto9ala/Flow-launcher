package com.flowlauncher.app.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val letter: Char
)

data class AppFolder(
    val id: String,
    val name: String,
    val appPackageNames: List<String>
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class LauncherSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showClock: Boolean = true,
    val use24HourClock: Boolean = true,
    val showStatusBar: Boolean = true,
    val doubleTapToLock: Boolean = true,
    val showWeather: Boolean = true,
    val accentColor: Long = 0xFF6C63FF
)
