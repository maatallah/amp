@echo off
echo ==========================================
echo AMP: Android Automotive & DHU Launcher
echo ==========================================

echo [1/3] Starting Android Virtual Device (AMP_Car_Emulator)...
start /B emulator -avd AMP_Car_Emulator -writable-system -no-snapshot

echo Waiting for device to boot...
adb wait-for-device

echo [2/3] Setting up ADB port forwarding for Android Auto DHU...
adb forward tcp:5277 tcp:5277
adb reverse tcp:8081 tcp:8081

echo [3/3] Launching Desktop Head Unit (DHU)...
if exist "%ANDROID_HOME%\extras\google\auto\desktop-head-unit.exe" (
    "%ANDROID_HOME%\extras\google\auto\desktop-head-unit.exe"
) else (
    echo DHU executable not found in %ANDROID_HOME%\extras\google\auto\
    echo Please install Android Auto Desktop Head Unit via SDK Manager.
)
pause
