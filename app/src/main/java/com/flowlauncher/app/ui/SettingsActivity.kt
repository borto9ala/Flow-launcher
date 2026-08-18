package com.flowlauncher.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowlauncher.app.data.ThemeMode
import com.flowlauncher.app.ui.components.SettingsScreen
import com.flowlauncher.app.ui.theme.FlowLauncherTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val vm: LauncherViewModel = viewModel()
            val uiState by vm.uiState.collectAsState()

            val darkTheme = when (uiState.settings.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            FlowLauncherTheme(darkTheme = darkTheme, accentColor = uiState.settings.accentColor) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SettingsScreen(
                        settings = uiState.settings,
                        hiddenApps = uiState.apps.filter { it.packageName in uiState.hiddenPackages },
                        onUpdateSettings = vm::updateSettings,
                        onUnhideApp = vm::unhideApp,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}
