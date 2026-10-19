@echo off
echo ===================================================
echo AMP: Context-Aware Automotive Music Player Simulation
echo ===================================================

echo [1/4] Booting Android Automotive Emulator (AMP_Car_Emulator)...
start /B emulator -avd AMP_Car_Emulator -writable-system -no-snapshot

echo Waiting for device connection...
adb wait-for-device

echo [2/4] Building and installing Debug APK...
cd android
call .\gradlew.bat assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
cd ..

echo [3/4] Establishing ADB port forwarding for Android Auto DHU...
adb forward tcp:5277 tcp:5277
adb reverse tcp:8081 tcp:8081

echo [4/4] Launching Desktop Head Unit (DHU)...
if exist "%ANDROID_HOME%\extras\google\auto\desktop-head-unit.exe" (
    "%ANDROID_HOME%\extras\google\auto\desktop-head-unit.exe" -c car_config.ini
) else (
    echo DHU executable not found in %ANDROID_HOME%\extras\google\auto\
    echo Please install Android Auto Desktop Head Unit via Android Studio SDK Manager.
)

pause
