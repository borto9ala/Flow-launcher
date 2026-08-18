package com.flowlauncher.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flowlauncher.app.data.AppFolder
import com.flowlauncher.app.data.AppInfo
import com.flowlauncher.app.data.AppRepository
import com.flowlauncher.app.data.LauncherSettings
import com.flowlauncher.app.data.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val apps: List<AppInfo> = emptyList(),
    val favorites: List<AppInfo> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val hiddenPackages: Set<String> = emptySet(),
    val settings: LauncherSettings = LauncherSettings(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedFolder: AppFolder? = null,
    val contextMenuApp: AppInfo? = null
) {
    val visibleApps: List<AppInfo>
        get() {
            val folderApps = folders.flatMap { it.appPackageNames }.toSet()
            val filtered = apps.filter { it.packageName !in hiddenPackages && it.packageName !in folderApps }
            return if (searchQuery.isBlank()) filtered
            else filtered.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }

    val alphabetIndex: Map<Char, Int>
        get() {
            val map = linkedMapOf<Char, Int>()
            visibleApps.forEachIndexed { index, app ->
                if (app.letter !in map) map[app.letter] = index
            }
            return map
        }
}

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val prefsRepository = PreferencesRepository(application)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFolder = MutableStateFlow<AppFolder?>(null)
    private val _contextMenuApp = MutableStateFlow<AppInfo?>(null)
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _isLoading = MutableStateFlow(true)

    val uiState: StateFlow<HomeUiState> = combine(
        combine(_apps, prefsRepository.favorites, prefsRepository.folders) { apps, favorites, folders ->
            Triple(apps, favorites, folders)
        },
        combine(prefsRepository.hiddenApps, prefsRepository.settings, _searchQuery) { hidden, settings, query ->
            Triple(hidden, settings, query)
        },
        combine(_isLoading, _selectedFolder, _contextMenuApp) { loading, folder, contextApp ->
            Triple(loading, folder, contextApp)
        }
    ) { appsData, prefsData, uiData ->
        val (apps, favorites, folders) = appsData
        val (hidden, settings, query) = prefsData
        val (loading, folder, contextApp) = uiData

        HomeUiState(
            apps = apps,
            favorites = apps.filter { it.packageName in favorites },
            folders = folders,
            hiddenPackages = hidden,
            settings = settings,
            searchQuery = query,
            isLoading = loading,
            selectedFolder = folder,
            contextMenuApp = contextApp
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        refreshApps()
    }

    fun refreshApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _apps.value = appRepository.loadLaunchableApps()
            _isLoading.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun launchApp(packageName: String) {
        appRepository.launchApp(packageName)
    }

    fun toggleFavorite(packageName: String) {
        viewModelScope.launch { prefsRepository.toggleFavorite(packageName) }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch { prefsRepository.setHidden(packageName, true) }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch { prefsRepository.setHidden(packageName, false) }
    }

    fun showContextMenu(app: AppInfo?) {
        _contextMenuApp.value = app
    }

    fun openFolder(folder: AppFolder?) {
        _selectedFolder.value = folder
    }

    fun createFolder(name: String, appPackageNames: List<String>) {
        viewModelScope.launch {
            prefsRepository.saveFolder(
                AppFolder(id = UUID.randomUUID().toString(), name = name, appPackageNames = appPackageNames)
            )
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch { prefsRepository.deleteFolder(folderId) }
    }

    fun updateSettings(settings: LauncherSettings) {
        viewModelScope.launch { prefsRepository.updateSettings(settings) }
    }

    fun openAppSettings(packageName: String) {
        appRepository.openAppSettings(packageName)
    }
}
