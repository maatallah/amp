# Developer & User Testing Guide: Context-Aware Automotive Music Player (AMP)

This guide provides step-by-step instructions for building, installing, and testing the Context-Aware Automotive Music Player on both physical Android devices/head units and Android Automotive OS (AAOS) emulators or Desktop Head Unit (DHU).

---

## Part 1: Prerequisites
1. **Java Development Kit (JDK 21)**: Hard-locked to Android Studio JBR (`C:\Program Files\Android\Android Studio\jbr`).
2. **Android SDK & Build Tools**: API 34 installed.
3. **Node.js & npm**: For running the React Native Metro bundler.
4. **ADB (Android Debug Bridge)**: Available in your system PATH.

---

## Part 2: Testing on a Physical Android Device / Head Unit

### 1. Enable Developer Options & USB Debugging
- On your Android device or head unit, go to **Settings > About Phone/Device**.
- Tap **Build Number** 7 times until "You are now a developer!" appears.
- Go to **Settings > Developer Options** and enable **USB Debugging**.

### 2. Connect via USB or Wi-Fi Debugging
- Connect your device via USB cable or wireless debugging.
- Verify connection:
  ```bash
  adb devices
  ```

### 3. Build & Install Debug APK
```bash
cd android
.\gradlew.bat installDebug
```

### 4. Start Metro Bundler & Connect App
- In the project root directory, start the React Native development server:
  ```bash
  npm start
  ```
- If your device is connected via USB, forward the Metro port:
  ```bash
  adb reverse tcp:8081 tcp:8081
  ```
- Launch the **Auto Music Player** app from your device launcher. You should see the React Native UI with localization toggles, theme switching, and music library scanner triggers.

---

## Part 3: Testing on Android Automotive OS (AAOS) Emulator & Desktop Head Unit (DHU)

### 1. Launch AAOS Emulator
- Open Android Studio Device Manager and launch an **Automotive OS API 34** emulator image.

### 2. Configure Port Forwarding & Reverse
- Forward necessary debugging sockets for AAOS media and React Native:
  ```bash
  adb forward tcp:5277 tcp:5277
  adb reverse tcp:8081 tcp:8081
  ```

### 3. Install App on Automotive Emulator
```bash
cd android
.\gradlew.bat installDebug
```

### 4. Test via Android Auto Desktop Head Unit (DHU) or Emulator Media Center
- Open the media app launcher inside the AAOS emulator screen.
- Verify that media playback services (`PlaybackService`), audio focus handling, and car app services (`AutoCarAppService`) initialize correctly without crashing.
