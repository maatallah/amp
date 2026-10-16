# Context-Aware Automotive Music Player (AMP)

A state-of-the-art, context-aware audio playback application built with **React Native (New Architecture)** and native **Android Automotive OS (AAOS)** integration.

---

## 1. Architectural Layout Matrix

The project cleanly separates user interface logic from background audio processing and native system integrations:

| Layer | Technology Stack | Responsibilities |
| :--- | :--- | :--- |
| **Frontend UI** | React Native 0.87, TypeScript, Context API | User interaction, theme switching (`ThemeContext`), language localization (`LocalizationContext`), and scanner triggers. |
| **Native Bridges** | Kotlin, JSI / Native Modules | Exposing local file discovery and metadata scanning [`MusicScannerModule.kt`](android/app/src/main/java/com/automusicplayer/bridge/MusicScannerModule.kt:1) to JavaScript. |
| **Audio Engine** | AndroidX Media3 (`ExoPlayer`, `MediaLibraryService`) | Persistent foreground playback, audio focus management, media session broadcasting, and Android Auto media tree support [`PlaybackService.kt`](android/app/src/main/java/com/automusicplayer/media/PlaybackService.kt:1). |
| **Automotive UX** | AndroidX Car App Library (`CarAppService`) | Touchscreen integration broker service [`AutoCarAppService.kt`](android/app/src/main/java/com/automusicplayer/car/AutoCarAppService.kt:1) paired with [`automotive_app_desc.xml`](android/app/src/main/res/xml/automotive_app_desc.xml:1). |
| **Persistence** | Room SQLite Persistence | Relational database storage for tracks, albums, artists, genres, and custom tags [`AppDatabase.kt`](android/app/src/main/java/com/automusicplayer/database/AppDatabase.kt:1) & [`MusicDao.kt`](android/app/src/main/java/com/automusicplayer/database/MusicDao.kt:1). |

---

## 2. Local Storage Relational Database Schema Model

The Room database (`auto_music_player.db`) maintains strict foreign key constraints and performance indices:

- **`artists`**: Primary key `id`, column `name`.
- **`albums`**: Primary key `id`, columns `name`, `art_path`, `year`.
- **`genres`**: Primary key `id`, columns `name`, `ignored`.
- **`tracks`**:
  - Primary key `id`.
  - Columns: `file_path`, `title`, `duration`, `bitrate`, `file_size`, `date_added`, `last_played`, `play_count`.
  - Foreign Keys: `album_id` (references `albums.id`), `artist_id` (references `artists.id`), `genre_id` (references `genres.id`).
  - Indices: `idx_tracks_artist_album` (`artist_id`, `album_id`), `idx_tracks_title` (`title`).
- **`custom_tag_map`**:
  - Primary key `id`.
  - Foreign Key: `track_id` (references `tracks.id`, cascade delete).
  - Columns: `tag_key`, `tag_value`.

---

## 3. Android Auto DHU Port-Forwarding Instructions

To connect the Desktop Head Unit (DHU) emulator with the React Native development server and debug bridge:

1. **Reverse Metro Bundler Port (`8081`)**:
   ```bash
   adb reverse tcp:8081 tcp:8081
   ```
2. **Forward Automotive Connection Port (`5277`)**:
   ```bash
   adb forward tcp:5277 tcp:5277
   ```

---

## 4. Compilation Environment Targets

- **Gradle Version**: `9.4.1` (configured with modern property assignment syntax `propName = value`).
- **Java Toolchain**: Hard-locked to Java 21 (`C:\\Program Files\\Android\\Android Studio\\jbr`) via `org.gradle.java.home` in [`gradle.properties`](android/gradle.properties:1) and `java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }` in [`app/build.gradle`](android/app/build.gradle:38).
- **Android SDK & MinSDK**: Compile SDK 34, Target SDK 34, Minimum SDK `24` (mandated by React Native 0.87.1 requirements).
- **ABIs**: NDK ABI filters restricted to `arm64-v8a` to ensure smooth 64-bit compilation and prevent Windows file locking conflicts.

---

## Quick Start & Verification

```bash
cd android
.\gradlew.bat clean assembleDebug
```
