# Flow Launcher Developer Notes

## Status

- Build environment fixed for this workspace.
- The project now includes a static UI preview in `preview-ui/index.html`.
- The main app is a Compose-based launcher with an alphabetic list UI.

## Key files

- `app/src/main/java/com/flowlauncher/app/ui/LauncherActivity.kt`
  - Compose host for the launcher UI.
- `app/src/main/java/com/flowlauncher/app/ui/LauncherViewModel.kt`
  - Holds app list state, favorites, hidden apps, and settings actions.
- `app/src/main/java/com/flowlauncher/app/ui/components/HomeScreen.kt`
  - Main launcher screen composable and app context sheet.
- `app/src/main/java/com/flowlauncher/app/ui/components/WaveAlphabet.kt`
  - Side alphabet fast-scroller UI.
- `app/src/main/java/com/flowlauncher/app/data/AppRepository.kt`
  - App metadata and install-state source.
- `app/src/main/java/com/flowlauncher/app/data/PreferencesRepository.kt`
  - DataStore-based user preferences and pinned/hidden apps.

## Build notes

- Use a compatible JDK, ideally JDK 17.
- If Gradle claims the Java version is too new, add or update `org.gradle.java.home` in `gradle.properties`.
- Example build command:

```bash
./gradlew assembleDebug
```

- APK output path:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Preview

Open `preview-ui/index.html` in a desktop browser to view a static mockup of the launcher home screen.

## Remaining polish tasks

- Ensure the launcher settings flow is complete and persists choices correctly.
- Add actual weather API integration or improve the placeholder weather component.
- Add a proper search field and keyboard-driven search behavior.
- Improve folder creation and management behavior.
- Add a clear hidden app management UI.

## Notes for handoff

- The UI preview is intentionally static and does not reflect live Compose state.
- Use `preview-ui/index.html` as a demonstration artifact for the launcher UX.
- The project is best continued by refining `HomeScreen.kt`, `SettingsScreen.kt`, and the data repository layers.
