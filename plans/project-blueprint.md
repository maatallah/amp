# Project Blueprint & Architecture: Context-Aware Automotive Music Player for Android Auto

## 1. Technology Stack Recommendation & Compatibility Analysis

Given your preference for **JavaScript / TypeScript / React Native**, we must design a hybrid architecture. Android Auto's projection protocol (`androidx.car.app`) and modern audio playback (`androidx.media3`) do not have official first-class React Native UI renderers because automotive head units enforce strict native template safety constraints and lifecycle management.

### Recommended Hybrid Stack
* **Mobile Frontend & Management UI**: React Native (TypeScript) + Expo / Bare React Native CLI. Used for settings, custom sorting rules, theme toggling, multi-language localization dictionary, and browsing management.
* **Local Database Persistence**: OP-SQLite or WatermelonDB / `react-native-sqlite-storage`, sharing the exact relational schema defined in the specification (`tracks`, `albums`, `artists`, `genres`, `custom_tag_map`) with composite indexes on `[artist_id, album_id]` and `title` to ensure sub-16ms query times.
* **Automotive Head Unit Service (Native Android / Kotlin)**:
  * **Automotive Template UI**: `androidx.car.app` (`CarAppService`, `Screen`, `PaneTemplate`, `ItemList`, `Row`) to render safe, distraction-free UI on the car screen.
  * **Audio Playback & Media Session**: AndroidX Media3 (`MediaSessionService`, `ExoPlayer`) running in a persistent background foreground service. Handles audio focus, Bluetooth/steering wheel hardware keys, and resume-on-connect.
* **Bridge Communication**: React Native Native Modules / Event Emitter to synchronize playback state, trigger media scans, and update custom classification rules between the JS UI and the native background/automotive services.

---

## 2. Workspace Architecture & Folder Hierarchy

```
AutoMusicPlayer/
├── android/                        # Native Android project (Kotlin)
│   ├── app/src/main/
│   │   ├── java/com/automusicplayer/
│   │   │   ├── car/                # CarAppService & Automotive Screens
│   │   │   ├── media/              # Media3 MediaSessionService & ExoPlayer
│   │   │   ├── database/           # SQLite OpenHelper / DAOs
│   │   │   └── bridge/             # React Native Native Modules
│   │   ├── res/
│   │   │   ├── xml/                # automotive_app_desc.xml
│   │   │   └── values/             # strings.xml (i18n)
│   │   └── AndroidManifest.xml
├── src/                            # React Native TypeScript Frontend
│   ├── components/                 # UI components (Theme-aware)
│   ├── screens/                    # Mobile Settings & Library Screens
│   ├── database/                   # Database client / TypeScript models
│   ├── localization/               # i18n dictionaries (en, etc.)
│   ├── services/                   # Bridge listeners & state management
│   └── App.tsx
├── package.json
└── tsconfig.json
```

---

## 3. Automotive Manifest & Metadata Configuration

### `android/app/src/main/AndroidManifest.xml` (Excerpt)
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />

    <application>
        <!-- Android Auto Automotive App Service Declaration -->
        <service
            android:name=".car.AutoCarAppService"
            android:exported="true">
            <intent-filter>
                <action android:name="androidx.car.app.CarAppService" />
            </intent-filter>
        </service>

        <!-- Automotive Metadata Declaration -->
        <meta-data
            android:name="androidx.car.app.application.automotive.modules"
            android:resource="@xml/automotive_app_desc" />

        <!-- Media3 Background Playback Service -->
        <service
            android:name=".media.PlaybackService"
            android:exported="true"
            android:foregroundServiceType="mediaPlayback">
            <intent-filter>
                <action android:name="androidx.media3.session.MediaSessionService" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

### `android/app/src/main/res/xml/automotive_app_desc.xml`
```xml
<automotiveApp>
    <uses name="media" />
</automotiveApp>
```

---

## 4. Relational Database Schema & SQLite DDL

```sql
CREATE TABLE IF NOT EXISTS artists (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL COLLATE NOCASE
);

CREATE TABLE IF NOT EXISTS albums (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL COLLATE NOCASE,
    art_path TEXT,
    year INTEGER
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL COLLATE NOCASE,
    ignored BOOLEAN DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tracks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    file_path TEXT UNIQUE NOT NULL,
    title TEXT NOT NULL COLLATE NOCASE,
    duration INTEGER NOT NULL,
    bitrate INTEGER,
    file_size INTEGER,
    date_added INTEGER,
    last_played INTEGER,
    play_count INTEGER DEFAULT 0,
    album_id INTEGER,
    artist_id INTEGER,
    genre_id INTEGER,
    FOREIGN KEY (album_id) REFERENCES albums(id),
    FOREIGN KEY (artist_id) REFERENCES artists(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS custom_tag_map (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    track_id INTEGER NOT NULL,
    tag_key TEXT NOT NULL,
    tag_value TEXT NOT NULL,
    FOREIGN KEY (track_id) REFERENCES tracks(id)
);

-- Performance Indexes (< 16ms query guarantee)
CREATE INDEX IF NOT EXISTS idx_tracks_artist_album ON tracks(artist_id, album_id);
CREATE INDEX IF NOT EXISTS idx_tracks_title ON tracks(title);
CREATE INDEX IF NOT EXISTS idx_artists_name ON artists(name);
CREATE INDEX IF NOT EXISTS idx_albums_name ON albums(name);
CREATE INDEX IF NOT EXISTS idx_genres_name ON genres(name);
```
