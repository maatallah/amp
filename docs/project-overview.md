# Project Overview: Context-Aware Automotive Music Player (AMP)

## Introduction
The Context-Aware Automotive Music Player (AMP) is a specialized React Native application engineered specifically for Android Automotive OS (AAOS) and modern mobile Android environments. It combines robust local audio playback capabilities (powered by AndroidX Media3 ExoPlayer) with an intelligent context-aware architecture (local metadata persistence via Room, automatic music scanning, dynamic theming, and multi-language localization).

## Core Architecture
- **Framework**: React Native (New Architecture enabled, React 0.87.1).
- **Native Language**: Kotlin (Java 21 toolchain target).
- **Build System**: Gradle 9.4.1 with modern property syntax assignments.
- **Audio Pipeline**: AndroidX Media3 (`media3-exoplayer` & `media3-session`) running inside a persistent foreground [`PlaybackService`](android/app/src/main/java/com/automusicplayer/media/PlaybackService.kt:1).
- **Automotive Integration**: AndroidX Car App Library (`androidx.car.app:app:1.4.0`) integrated via [`AutoCarAppService`](android/app/src/main/java/com/automusicplayer/car/AutoCarAppService.kt:1) and [`automotive_app_desc.xml`](android/app/src/main/res/xml/automotive_app_desc.xml:1).
- **Persistence & Interop**: Room Relational Persistence (`AppDatabase.kt`) and Custom React Native Native Modules (`MusicScannerModule.kt`).
