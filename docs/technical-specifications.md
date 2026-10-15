# Technical Specifications & Environment Configuration

## Build & Toolchain Requirements
- **JDK / Java Version**: Java 21 (Hard-locked to Android Studio JBR at `C:\Program Files\Android\Android Studio\jbr`).
- **Gradle Version**: Gradle 9.4.1 (configured via wrapper properties).
- **Android SDK**: Compile SDK 34, Target SDK 34, Minimum SDK 24.
- **Kotlin Version**: 2.0.0.

## Key Configuration Files
1. [`android/settings.gradle`](android/settings.gradle:1): Manages React Native autolinking plugin management.
2. [`android/build.gradle`](android/build.gradle:1): Defines project extension constants (`minSdkVersion = 24`, `compileSdkVersion = 34`, `kotlinVersion = "2.0.0"`).
3. [`android/app/build.gradle`](android/app/build.gradle:1): Configures application module, Java 21 toolchain parameters (`JavaLanguageVersion.of(21)`), NDK ABI filters (`arm64-v8a`), and dependencies (`Media3`, `Car App Library`, `Room`).
4. [`android/app/src/main/AndroidManifest.xml`](android/app/src/main/AndroidManifest.xml:1): Declares permissions (`READ_MEDIA_AUDIO`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`), Activities ([`MainActivity`](android/app/src/main/java/com/automusicplayer/MainActivity.kt:1)), and Services ([`PlaybackService`](android/app/src/main/java/com/automusicplayer/media/PlaybackService.kt:1), [`AutoCarAppService`](android/app/src/main/java/com/automusicplayer/car/AutoCarAppService.kt:1)) with standard `http://schemas.android.com/apk/res/android` namespace.
5. [`android/gradle.properties`](android/gradle.properties:1): Hard-locks `org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr` and AndroidX flags.
