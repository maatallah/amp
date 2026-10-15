# Agent Handover Document

## Welcome, Incoming Agent!
This document serves as your operational guide for taking over the **Context-Aware Automotive Music Player (AMP)** project repository (`https://github.com/maatallah/amp`).

## Current State & Verification
- **Build Status**: Fully validated and successfully compiled to a green `BUILD SUCCESSFUL` state on Windows 11.
- **Repository**: Tracked on GitHub (`origin/main`).

## Getting Started & Development Workflow
1. **Prerequisites**: Ensure Java 21 (Android Studio JBR) is installed and configured.
2. **Building the Android App**:
   ```bash
   cd android
   .\gradlew.bat assembleDebug
   ```
3. **Running the React Native Metro Server**:
   ```bash
   npm start
   ```

## Key Code Locations
- **Android Entry Points**: [`android/app/src/main/java/com/automusicplayer/MainActivity.kt`](android/app/src/main/java/com/automusicplayer/MainActivity.kt:1) & [`MainApplication.kt`](android/app/src/main/java/com/automusicplayer/MainApplication.kt:1).
- **Media & Car Services**: [`android/app/src/main/java/com/automusicplayer/media/PlaybackService.kt`](android/app/src/main/java/com/automusicplayer/media/PlaybackService.kt:1) & [`AutoCarAppService.kt`](android/app/src/main/java/com/automusicplayer/car/AutoCarAppService.kt:1).
- **TypeScript Frontend**: [`src/App.tsx`](src/App.tsx:1), [`src/context/LocalizationContext.tsx`](src/context/LocalizationContext.tsx:1), [`src/context/ThemeContext.tsx`](src/context/ThemeContext.tsx:1).

## Maintenance Rules
- Never alter the Android namespace URI in [`AndroidManifest.xml`](android/app/src/main/AndroidManifest.xml:1) away from `http://schemas.android.com/apk/res/android`.
- Maintain Java 21 toolchain blocks in gradle configuration files.
- Refer to `docs/project-overview.md` and `docs/technical-specifications.md` for architectural context.
