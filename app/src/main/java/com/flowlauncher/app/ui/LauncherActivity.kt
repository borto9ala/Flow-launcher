package com.flowlauncher.app.ui

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flowlauncher.app.data.ThemeMode
import com.flowlauncher.app.ui.components.HomeScreen
import com.flowlauncher.app.ui.theme.FlowLauncherTheme

class LauncherActivity : ComponentActivity() {

    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
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

            if (!uiState.settings.showStatusBar) {
                hideStatusBar()
            } else {
                showStatusBar()
            }

            FlowLauncherTheme(
                darkTheme = darkTheme,
                accentColor = uiState.settings.accentColor
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen(
                        uiState = uiState,
                        onSearch = vm::setSearchQuery,
                        onLaunchApp = vm::launchApp,
                        onToggleFavorite = vm::toggleFavorite,
                        onHideApp = vm::hideApp,
                        onContextMenu = vm::showContextMenu,
                        onOpenFolder = vm::openFolder,
                        onDismissFolder = { vm.openFolder(null) },
                        onDismissContextMenu = { vm.showContextMenu(null) },
                        onOpenSettings = { startActivity(Intent(this, SettingsActivity::class.java)) },
                        onOpenAppSettings = vm::openAppSettings,
                        onDoubleTap = { handleDoubleTap(uiState.settings.doubleTapToLock) },
                        onRefresh = vm::refreshApps
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload apps when returning to launcher
    }

    private fun handleDoubleTap(enabled: Boolean) {
        if (!enabled) return
        val now = System.currentTimeMillis()
        if (now - lastTapTime < 300) {
            lockScreen()
            vibrate()
        }
        lastTapTime = now
    }

    private fun lockScreen() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (dpm.isAdminActive(ComponentName(this, ScreenLockAdminReceiver::class.java))) {
            dpm.lockNow()
            return
        }
        // Fallback: turn screen off via window flag (requires permission on newer Android)
        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }

    private fun hideStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showStatusBar() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.statusBars())
    }
}
