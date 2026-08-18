package com.flowlauncher.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("flow_launcher_prefs")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorites")
        val HIDDEN = stringSetPreferencesKey("hidden")
        val FOLDERS = stringPreferencesKey("folders")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val USE_24H = booleanPreferencesKey("use_24h")
        val SHOW_STATUS_BAR = booleanPreferencesKey("show_status_bar")
        val DOUBLE_TAP_LOCK = booleanPreferencesKey("double_tap_lock")
        val SHOW_WEATHER = booleanPreferencesKey("show_weather")
        val ACCENT_COLOR = longPreferencesKey("accent_color")
    }

    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }
    val hiddenApps: Flow<Set<String>> = context.dataStore.data.map { it[Keys.HIDDEN] ?: emptySet() }

    val folders: Flow<List<AppFolder>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.FOLDERS] ?: return@map emptyList()
        parseFolders(raw)
    }

    val settings: Flow<LauncherSettings> = context.dataStore.data.map { prefs ->
        LauncherSettings(
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
            showClock = prefs[Keys.SHOW_CLOCK] ?: true,
            use24HourClock = prefs[Keys.USE_24H] ?: true,
            showStatusBar = prefs[Keys.SHOW_STATUS_BAR] ?: true,
            doubleTapToLock = prefs[Keys.DOUBLE_TAP_LOCK] ?: true,
            showWeather = prefs[Keys.SHOW_WEATHER] ?: true,
            accentColor = prefs[Keys.ACCENT_COLOR] ?: 0xFF6C63FF
        )
    }

    suspend fun toggleFavorite(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(packageName)) current.remove(packageName)
            prefs[Keys.FAVORITES] = current
        }
    }

    suspend fun setHidden(packageName: String, hidden: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.HIDDEN]?.toMutableSet() ?: mutableSetOf()
            if (hidden) current.add(packageName) else current.remove(packageName)
            prefs[Keys.HIDDEN] = current
        }
    }

    suspend fun saveFolder(folder: AppFolder) {
        context.dataStore.edit { prefs ->
            val current = parseFolders(prefs[Keys.FOLDERS] ?: "[]").toMutableList()
            current.removeAll { it.id == folder.id }
            current.add(folder)
            prefs[Keys.FOLDERS] = serializeFolders(current)
        }
    }

    suspend fun deleteFolder(folderId: String) {
        context.dataStore.edit { prefs ->
            val current = parseFolders(prefs[Keys.FOLDERS] ?: "[]").filter { it.id != folderId }
            prefs[Keys.FOLDERS] = serializeFolders(current)
        }
    }

    suspend fun updateSettings(settings: LauncherSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.SHOW_CLOCK] = settings.showClock
            prefs[Keys.USE_24H] = settings.use24HourClock
            prefs[Keys.SHOW_STATUS_BAR] = settings.showStatusBar
            prefs[Keys.DOUBLE_TAP_LOCK] = settings.doubleTapToLock
            prefs[Keys.SHOW_WEATHER] = settings.showWeather
            prefs[Keys.ACCENT_COLOR] = settings.accentColor
        }
    }

    private fun parseFolders(raw: String): List<AppFolder> {
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        AppFolder(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            appPackageNames = obj.getJSONArray("apps").let { apps ->
                                List(apps.length()) { apps.getString(it) }
                            }
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun serializeFolders(folders: List<AppFolder>): String {
        val array = JSONArray()
        folders.forEach { folder ->
            array.put(
                JSONObject().apply {
                    put("id", folder.id)
                    put("name", folder.name)
                    put("apps", JSONArray(folder.appPackageNames))
                }
            )
        }
        return array.toString()
    }
}
