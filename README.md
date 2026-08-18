# Flow Launcher

A free, open-source Android launcher inspired by Niagara Launcher's minimalist list-based design.

## Features

- **List-based home screen** — Clean vertical app list instead of a cluttered grid
- **Wave alphabet** — Drag the side alphabet to jump to apps instantly (one-handed use)
- **Clock & date** — Large clock widget at the top
- **Weather widget** — Built-in weather display (placeholder; connect a weather API for live data)
- **Favorites** — Pin apps to the top (long-press any app)
- **Pop-up folders** — Organize apps into folders
- **Search** — Quick app search
- **Hide apps** — Remove apps from the list without uninstalling
- **Themes** — Light, dark, and system theme modes
- **Gestures** — Double-tap to lock screen
- **100% free** — No subscriptions, no ads, no paywalls

## Build

Requirements: Android SDK, JDK 17+

Build troubleshooting
---------------------

If the Gradle build fails during script evaluation with an error mentioning a Java version (for example `java.lang.IllegalArgumentException: 25.0.4`), your system JDK is newer than the Kotlin/Gradle toolchain expects. To fix this locally one of the following is recommended:

- Install a compatible JDK (adoptopenjdk / Temurin JDK 17 or JDK 21) and point Gradle to it via `org.gradle.java.home` in `gradle.properties`:

```properties
org.gradle.java.home=/path/to/jdk17
```

- Or run the build using a compatible `JAVA_HOME` in the shell:

```bash
export JAVA_HOME=/path/to/jdk17
./gradlew assembleDebug
```

After fixing the JDK, run `./gradlew assembleDebug` again.

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Preview UI

A static preview of the launcher UI is available in the `preview-ui/` folder. Open `preview-ui/index.html` in a browser to see a web-based demo of the launcher layout.

## Install

1. Build or download the APK
2. Install on your Android device
3. Press Home — select **Flow Launcher** as your default launcher

## Usage

- **Tap** an app to launch it
- **Long-press** an app for options (favorite, hide)
- **Drag** the alphabet on the right edge to scroll quickly
- **Double-tap** empty space to lock screen
- **Search icon** (top right) to find apps
- **Settings icon** to customize appearance and gestures

## License

MIT — free to use and modify.
# flow-launcher
# flow-launcher
