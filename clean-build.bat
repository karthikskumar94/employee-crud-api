@echo off
echo Stopping Gradle daemons...
gradlew.bat --stop

echo Waiting for processes to release file handles...
timeout /t 3 /nobreak > nul

echo Attempting to delete build directory...
if exist "build" (
    rmdir /s /q "build" 2>nul
    if exist "build" (
        echo Build directory still exists, trying PowerShell...
        powershell -Command "Remove-Item -Path 'build' -Recurse -Force -ErrorAction SilentlyContinue"
    )
)

echo Running clean build...
gradlew.bat clean build

echo Clean build completed!
pause
