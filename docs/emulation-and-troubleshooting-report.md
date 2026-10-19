# Comprehensive Troubleshooting & Resolution Report: Context-Aware Automotive Music Player (AMP)

This report provides a detailed chronological ledger of every critical blocker encountered during project initialization, compilation, and emulation, along with the exact engineering resolutions applied.

---

## Summary of All Issues & Fixes

### 1. Gradle Engine & Property Definition Incompatibilities
- **Issue**: Initial builds failed due to legacy whitespace assignments in Gradle templates and mismatched Gradle wrapper distributions.
- **Fix**: Upgraded/locked Gradle to version `9.4.1`, enforced modern property assignment syntax (`propName = value`), and hard-locked `org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr` in [`android/gradle.properties`](android/gradle.properties:1).

### 2. Manifest Merger Failures (`Missing 'name' key attribute`)
- **Issue**: The Android build system rejected [`AndroidManifest.xml`](android/app/src/main/AndroidManifest.xml:1) with fatal errors claiming `android:name` was missing across all permissions, activities, and services.
- **Fix**: Discovered that the namespace URI had been incorrectly specified as `xmlns:android="http://android.com"`. Corrected it to the mandatory standard Android schema:
  `xmlns:android="http://schemas.android.com/apk/res/android"`.

### 3. Minimum SDK Version Mismatch vs React Native 0.87.1
- **Issue**: Gradle compilation failed because project `minSdk` was set to `23`, while dependency [`com.facebook.react:react-android:0.87.1`](android/app/build.gradle:23) strictly required `minSdk 24`.
- **Fix**: Elevated project `minSdk` / `minSdkVersion` to `24` in [`android/build.gradle`](android/build.gradle:1) and [`android/app/build.gradle`](android/app/build.gradle:1).

### 4. Windows File Locking During Native C++ CMake Builds
- **Issue**: Multi-ABI compilation (`arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`) triggered `Permission denied` file deletion errors (`ninja: error: remove(...)`) on Windows due to concurrent locks on precompiled binaries.
- **Fix**: Restricted NDK ABI filters to `arm64-v8a` (`ndk { abiFilters "arm64-v8a" }`) in [`android/app/build.gradle`](android/app/build.gradle:1) to stabilize 64-bit ARM automotive compilation.

### 5. Missing Native Application Bootloaders (`MainActivity.kt` & `MainApplication.kt`)
- **Issue**: The compiled binary crashed immediately on launch because Java/Kotlin runtime could not locate `com.automusicplayer.MainActivity`.
- **Fix**: Created [`MainActivity.kt`](android/app/src/main/java/com/automusicplayer/MainActivity.kt:1) and [`MainApplication.kt`](android/app/src/main/java/com/automusicplayer/MainApplication.kt:1) with proper component registration and delegate hooks.

### 6. New Architecture Crash on Windows / 32-bit Host Environments
- **Issue**: `DefaultNewArchitectureEntryPoint.load()` and `fabricEnabled` triggered runtime initialization crashes on certain host environments.
- **Fix**: Removed New Architecture entrypoint loader calls and explicitly set `fabricEnabled = false` in [`MainActivity.kt`](android/app/src/main/java/com/automusicplayer/MainActivity.kt:1) to ensure absolute runtime stability.

### 7. Offline Asset Bundling Failure on USB Dropouts
- **Issue**: Intermittent physical USB debugging drops caused blank layout rendering due to missing local Metro runtime connection.
- **Fix**: Generated the production offline JavaScript bundle (`index.android.bundle`) into `android/app/src/main/assets/` via `npx react-native bundle`.
