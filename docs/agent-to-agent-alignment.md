# AGENT-TO-AGENT REPOSITORY ALIGNMENT & AUTO-VERIFICATION PROTOCOL

**TARGET AGENT**: Autonomous AI Coding Agent (VS Code Workspace)  
**PROJECT REPOSITORY**: `https://github.com/maatallah/amp` (`main`)  

---

## 1. Automated System Inspection Protocol
Upon taking over this workspace, execute the following diagnostic checks immediately via tool calls to align your local environment:

1. **Verify Java Toolchain & Environment**:
   - Check that `JAVA_HOME` or `org.gradle.java.home` points to Java 21 (Android Studio JBR at `C:\Program Files\Android\Android Studio\jbr`).
2. **Verify Gradle & AGP Versions**:
   - Confirm Gradle wrapper is locked to `9.4.1` (`android/gradle/wrapper/gradle-wrapper.properties`).
   - Confirm AGP version is `8.4.2` (`android/build.gradle`).
3. **Verify Manifest Namespace & MinSDK**:
   - Inspect [`android/app/src/main/AndroidManifest.xml`](android/app/src/main/AndroidManifest.xml:1) to ensure `xmlns:android="http://schemas.android.com/apk/res/android"` is strictly maintained.
   - Inspect [`android/app/build.gradle`](android/app/build.gradle:1) to ensure `minSdk = 24`.

---

## 2. Automated Build & Stack Alignment Script
If any discrepancy is detected during inspection, execute the following alignment sequence programmatically:

```bash
# 1. Stop any dangling Gradle daemons
cd android
.\gradlew.bat --stop

# 2. Run clean build assembly with offline asset verification
.\gradlew.bat clean assembleDebug
```

---

## 3. Critical Invariants (Do Not Modify)
- **New Architecture**: Must remain disabled (`newArchEnabled=false`, `fabricEnabled = false` in [`MainActivity.kt`](android/app/src/main/java/com/automusicplayer/MainActivity.kt:1)).
- **NDK ABIs**: Restricted to `arm64-v8a` (`ndk { abiFilters "arm64-v8a" }`) to prevent Windows file-locking (`ninja: error: remove(...)`) issues.
- **Offline Assets**: Pre-bundled JS asset exists at `android/app/src/main/assets/index.android.bundle`.
