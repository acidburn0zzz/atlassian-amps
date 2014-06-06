@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

set INPUT_DIR=%1
set OUTPUT_DIR=%2
set PASSWORD=%3

for /r %%i in (*.pfx) do (set PFX=%%~nxi)
for /r %%i in (*.cer) do (set INTERMEDIATE_CERT=%%~nxi)
set TIMESERVER="http://timestamp.verisign.com/scripts/timstamp.dll"
echo "Signing %FILENAME% with cert:%INTERMEDIATE_CERT% and pfx:%PFX%"

for /f %%f in ('dir /b  %INPUT_DIR%') do (
   set FILENAME=%%f
   set FILEPATH=%INPUT_DIR%\!FILENAME!
   echo !FILEPATH!
   move !FILEPATH! %CD%
   signtool.exe sign /f %PFX% /p %PASSWORD% /ac %INTERMEDIATE_CERT% /t %TIMESERVER% !FILENAME!
   move !FILENAME! %OUTPUT_DIR%\!FILENAME!
)


set /p DUMMY=Hit ENTER to continue...