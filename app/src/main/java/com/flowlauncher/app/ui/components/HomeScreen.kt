package com.flowlauncher.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flowlauncher.app.data.AppFolder
import com.flowlauncher.app.data.AppInfo
import com.flowlauncher.app.ui.HomeUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSearch: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onHideApp: (String) -> Unit,
    onContextMenu: (AppInfo?) -> Unit,
    onOpenFolder: (AppFolder?) -> Unit,
    onDismissFolder: () -> Unit,
    onDismissContextMenu: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppSettings: (String) -> Unit,
    onDoubleTap: () -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showSearch by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(start = 20.dp, end = 8.dp, top = 48.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.settings.showClock) {
                                ClockWidget(use24Hour = uiState.settings.use24HourClock)
                            }
                            Row {
                                IconButton(onClick = onRefresh) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh apps")
                                }
                                IconButton(onClick = { showSearch = !showSearch }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search")
                                }
                                IconButton(onClick = onOpenSettings) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.settings.showWeather) {
                        item {
                            WeatherWidget()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (uiState.favorites.isNotEmpty()) {
                        item {
                            Text(
                                "Favorites",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.favorites, key = { "fav-${it.packageName}" }) { app ->
                            AppListItem(
                                app = app,
                                isFavorite = true,
                                onClick = { onLaunchApp(app.packageName) },
                                onLongClick = { onContextMenu(app) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    if (uiState.folders.isNotEmpty()) {
                        item {
                            Text(
                                "Folders",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(uiState.folders, key = { "folder-${it.id}" }) { folder ->
                            FolderListItem(
                                folder = folder,
                                onClick = { onOpenFolder(folder) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    item {
                        Text(
                            "All Apps",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (uiState.visibleApps.isEmpty()) {
                        item {
                            Text(
                                "No apps found.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    }

                    items(uiState.visibleApps, key = { it.packageName }) { app ->
                        AppListItem(
                            app = app,
                            isFavorite = app.packageName in uiState.favorites.map { it.packageName },
                            onClick = { onLaunchApp(app.packageName) },
                            onLongClick = { onContextMenu(app) }
                        )
                    }
                }

                if (uiState.searchQuery.isBlank()) {
                    WaveAlphabet(
                        letters = uiState.alphabetIndex.keys.toList(),
                        onLetterSelected = { letter ->
                            uiState.alphabetIndex[letter]?.let { index ->
                                val scrollIndex = index + headerOffset(uiState)
                                scope.launch { listState.animateScrollToItem(scrollIndex) }
                            }
                        },
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight()
                            .padding(end = 4.dp, top = 120.dp, bottom = 24.dp)
                    )
                }
            }
        }

        if (showSearch) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearch,
                onDismiss = {
                    showSearch = false
                    onSearch("")
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }

    uiState.contextMenuApp?.let { app ->
        AppContextSheet(
            app = app,
            isFavorite = app.packageName in uiState.favorites.map { it.packageName },
            onDismiss = onDismissContextMenu,
            onToggleFavorite = {
                onToggleFavorite(app.packageName)
                onDismissContextMenu()
            },
            onOpenAppSettings = {
                onOpenAppSettings(app.packageName)
                onDismissContextMenu()
            },
            onHide = {
                onHideApp(app.packageName)
                onDismissContextMenu()
            }
        )
    }

    uiState.selectedFolder?.let { folder ->
        FolderSheet(
            folder = folder,
            apps = uiState.apps.filter { it.packageName in folder.appPackageNames },
            onDismiss = onDismissFolder,
            onLaunchApp = onLaunchApp
        )
    }
}

private fun headerOffset(uiState: HomeUiState): Int {
    var offset = 1 // clock row
    if (uiState.settings.showWeather) offset++
    if (uiState.favorites.isNotEmpty()) offset += uiState.favorites.size + 1
    if (uiState.folders.isNotEmpty()) offset += uiState.folders.size + 1
    offset++ // "All Apps" label
    return offset
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppInfo,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIcon(drawable = app.icon, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isFavorite) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun FolderListItem(folder: AppFolder, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(folder.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "${folder.appPackageNames.size} apps",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search apps...") },
                singleLine = true
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextSheet(
    app: AppInfo,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onHide: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIcon(drawable = app.icon, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(app.label, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            ContextAction(text = if (isFavorite) "Remove from favorites" else "Add to favorites", onClick = onToggleFavorite)
            ContextAction(text = "App info", onClick = onOpenAppSettings)
            ContextAction(text = "Hide app", onClick = onHide)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ContextAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderSheet(
    folder: AppFolder,
    apps: List<AppInfo>,
    onDismiss: () -> Unit,
    onLaunchApp: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(folder.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            apps.forEach { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLaunchApp(app.packageName); onDismiss() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(drawable = app.icon, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(app.label)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
