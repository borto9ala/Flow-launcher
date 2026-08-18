package com.flowlauncher.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flowlauncher.app.data.AppInfo
import com.flowlauncher.app.data.LauncherSettings
import com.flowlauncher.app.data.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: LauncherSettings,
    hiddenApps: List<AppInfo>,
    onUpdateSettings: (LauncherSettings) -> Unit,
    onUnhideApp: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionTitle("Appearance")
            SettingToggle("Show clock", settings.showClock) {
                onUpdateSettings(settings.copy(showClock = it))
            }
            SettingToggle("24-hour clock", settings.use24HourClock) {
                onUpdateSettings(settings.copy(use24HourClock = it))
            }
            SettingToggle("Show weather widget", settings.showWeather) {
                onUpdateSettings(settings.copy(showWeather = it))
            }
            SettingToggle("Show status bar", settings.showStatusBar) {
                onUpdateSettings(settings.copy(showStatusBar = it))
            }

            ThemeSelector(settings) { onUpdateSettings(it) }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Gestures")
            SettingToggle("Double tap to lock", settings.doubleTapToLock) {
                onUpdateSettings(settings.copy(doubleTapToLock = it))
            }

            if (hiddenApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Hidden Apps")
                hiddenApps.forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUnhideApp(app.packageName) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(drawable = app.icon, modifier = Modifier.size(32.dp))
                        Text(app.label, modifier = Modifier.padding(start = 12.dp).weight(1f))
                        Text("Unhide", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Flow Launcher — 100% free, open source",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ThemeSelector(settings: LauncherSettings, onUpdate: (LauncherSettings) -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Text("Theme", style = MaterialTheme.typography.bodyLarge)
    ThemeMode.entries.forEach { mode ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUpdate(settings.copy(themeMode = mode)) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val selected = settings.themeMode == mode
            Text(
                text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                modifier = Modifier.weight(1f),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            if (selected) Text("✓", color = MaterialTheme.colorScheme.primary)
        }
    }
}
