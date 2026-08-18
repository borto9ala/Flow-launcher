# Flow Launcher Work Plan

## Current status

This repo is now in a better state for handoff.
The build environment has been stabilized with a compatible JDK path, and a static UI preview has been added in `preview-ui/index.html`.

## Implemented features

- Compose-based launcher UI with vertical app list
- Alphabetic side scroll widget (`WaveAlphabet`)
- Clock + weather placeholder widgets
- Favorites section
- Folder list display and folder sheet UI
- Search bar overlay
- App context menu (favorite/hide)
- Settings screen with theme and gesture toggles
- Data persistence using `DataStore` for favorites, hidden apps, folders, and settings
- App launch via package manager
- Double-tap gesture for screen lock (requires device admin for full behavior)
- Static browser preview added in `preview-ui/index.html`
- Developer handoff notes created in `DEVELOPER_NOTES.md`

## Current notes

- The build environment is now stable for JDK 17-based Gradle/Kotlin execution.
- `README.md` now references the UI preview output folder.
- The preview is static and intended as a visual demo only.

## Recommended next step

1. Continue polish and feature completion:
   - Refine search and app list scrolling behavior.
   - Add folder creation/editing UI and hidden-app management.
   - Implement a real weather provider or remove placeholder logic.
   - Improve accessibility and settings persistence.

## Task tree

### 1. Environment / build

- [x] Standardize the JDK for this project
- [x] Verify Gradle wrapper works in this workspace
- [x] Add a `README` note about required JDK and Android SDK versions

### 2. Core launcher behavior

- [ ] Confirm `LauncherActivity` and `SettingsActivity` navigation flows
- [ ] Ensure `HomeScreen` uses `onRefresh` or lifecycle reload when returning from apps
- [ ] Fix search UX so search mode and typed query behave consistently
- [ ] Improve `WaveAlphabet` behavior to jump only to available letters
- [ ] Add list section support for headers and accurate scroll offsets

### 3. Missing UI/features

- [ ] Add a UI path for creating / editing folders
- [ ] Add an explicit unhide action in the main launcher UI
- [ ] Add app details/settings/uninstall actions in the context sheet
- [ ] Add actual weather integration or improve placeholder handling
- [ ] Add a launcher onboarding or first-run setup

### 4. Polish / product readiness

- [ ] Add accent color picker in settings
- [ ] Add app labeling or icon pack customization hooks
- [ ] Add hidden apps toggle and better folder management
- [ ] Improve accessibility and gesture affordances
- [ ] Add release build config and proguard optimization if desired

## Primary files to continue in

- `app/src/main/java/com/flowlauncher/app/ui/LauncherViewModel.kt`
- `app/src/main/java/com/flowlauncher/app/ui/components/HomeScreen.kt`
- `app/src/main/java/com/flowlauncher/app/ui/components/WaveAlphabet.kt`
- `app/src/main/java/com/flowlauncher/app/ui/components/SettingsScreen.kt`
- `app/src/main/java/com/flowlauncher/app/data/PreferencesRepository.kt`
- `app/src/main/java/com/flowlauncher/app/data/AppRepository.kt`
- `app/src/main/java/com/flowlauncher/app/ui/LauncherActivity.kt`
- `app/src/main/AndroidManifest.xml`

## Notes for developers

- The launcher is registered as a home/launcher activity in `AndroidManifest.xml`
- `PreferencesRepository` stores folders as JSON strings; that is a good place to add folder metadata or order
- `WaveAlphabet` currently always renders A–Z, but uses `letters` to indicate availability
- `AppIcon` converts `Drawable` to `ImageBitmap` on every composition; consider caching or using an image loader for performance
- `BootReceiver` exists but is stubbed; if boot behavior is needed, add service start logic there

## Status summary

- `HomeScreen` and supporting Compose components are the best place to continue work
- The repo now includes the requested static UI preview asset
- Remaining work is polish and feature completion rather than build/environment setup
