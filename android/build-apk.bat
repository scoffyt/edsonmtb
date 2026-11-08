@echo off
rem Compila o APK no Windows usando o Android SDK em C:\android-build
setlocal
cd /d %~dp0
set SDK=C:\android-build
if exist "%SDK%\sdk\platforms" set SDK=C:\android-build\sdk
if exist "%SDK%\Sdk\platforms" set SDK=C:\android-build\Sdk
set ANDROID_HOME=%SDK%
set ANDROID_SDK_ROOT=%SDK%
echo sdk.dir=%SDK:\=\\%> local.properties
echo Usando o Android SDK em %SDK%
copy /Y ..\index.html app\src\main\assets\ >nul
copy /Y ..\icone-192.png app\src\main\assets\ >nul
copy /Y ..\icone-512.png app\src\main\assets\ >nul
copy /Y ..\manifest.webmanifest app\src\main\assets\ >nul
call gradlew.bat --no-daemon assembleRelease
if errorlevel 1 (
  echo.
  echo Deu erro na compilacao. Confira se o JDK 17 esta instalado e se o SDK tem a plataforma 34.
  pause
  exit /b 1
)
copy /Y app\build\outputs\apk\release\app-release.apk ..\TrilhaDoEdson.apk >nul
echo.
echo Pronto: TrilhaDoEdson.apk esta na pasta do repositorio.
pause
