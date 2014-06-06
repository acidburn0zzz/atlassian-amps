REM This script is a convenience for signing windows executables

@echo off

REM This is needed to set variables inside for-loops
SETLOCAL ENABLEDELAYEDEXPANSION

REM The directory to search for executables
set INPUT_DIR=%1

REM The directory to place signed executables
set OUTPUT_DIR=%2

REM The password for the certificate
set PASSWORD=%3

REM This simply finds a single PFX signing file in the current directory
for /r %%i in (*.pfx) do (set PFX=%%~nxi)

REM Similarly finds a single intermediate certificate
for /r %%i in (*.cer) do (set INTERMEDIATE_CERT=%%~nxi)

set TIMESERVER="http://timestamp.verisign.com/scripts/timstamp.dll"
echo "Signing files with cert:%INTERMEDIATE_CERT% and pfx:%PFX%"

REM Exclamation marks are the way to expand variables set inside for-loop (see delayed expansion setting)
for /f %%f in ('dir /b  %INPUT_DIR%') do (
   set FILENAME=%%f
   set FILEPATH=%INPUT_DIR%\!FILENAME!
   echo !FILEPATH!
   move !FILEPATH! %CD%
   signtool.exe sign /f %PFX% /p %PASSWORD% /ac %INTERMEDIATE_CERT% /t %TIMESERVER% !FILENAME!
   move !FILENAME! %OUTPUT_DIR%\!FILENAME!
)

REM Keeps the window open for the user to check output
set /p DUMMY=Hit ENTER to continue...