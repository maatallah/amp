# New PC Setup & Stack Compatibility Verification Guide

Welcome! If you have cloned this repository (`https://github.com/maatallah/amp`) onto a brand-new PC and opened it in VS Code, follow this guide to verify your environment stack, install required dependencies, and build the project successfully without friction.

---

## 1. System Requirements & Prerequisites

Before opening the project, ensure your workstation has the following installed:
1. **Node.js (LTS version)** & **npm**: Required for React Native tooling and bundler.
2. **Android Studio (Koala or newer recommended)**: With Android SDK Platform 34 and Build-Tools installed.
3. **Java Development Kit (JDK 21)**: The project is hard-locked to Java 21 (ideally Android Studio's bundled JBR at `C:\Program Files\Android\Android Studio\jbr` on Windows).

---

## 2. Cloning & Initializing the Repository

Open your terminal and clone the repository:
```bash
git clone https://github.com/maatallah/amp.git
cd amp
```

Install Node modules:
```bash
npm install
```

---

## 3. Verifying Environment Variables & Java Toolchain

1. **Set `JAVA_HOME`**: Ensure your environment points to Java 21:
   - Windows (PowerShell):
     ```powershell
     $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
     ```
2. **Verify Gradle Properties**: Check [`android/gradle.properties`](android/gradle.properties:1) to ensure `org.gradle.java.home` matches your JDK 21 path.

---

## 4. Stack Compatibility Check & Build Verification

To verify that your new PC's build environment is fully compatible and ready:

1. Navigate to the android directory:
   ```bash
   cd android
   ```
2. Run a clean build assembly:
   ```bash
   .\gradlew.bat clean assembleDebug
   ```
3. A successful build will output `BUILD SUCCESSFUL` with all 50+ tasks completed.

---

## 5. Running the Application & Emulation

- **Offline Asset Bundle**: The project includes a pre-bundled offline JavaScript asset (`android/app/src/main/assets/index.android.bundle`), ensuring the app runs immediately even if USB debugging sockets drop.
- **Starting Metro & DHU**:
  ```bash
  npm start
  ```
  Or use the automated launcher script [`run-emulator-dhu.bat`](run-emulator-dhu.bat:1) to boot your AVD and Desktop Head Unit.
