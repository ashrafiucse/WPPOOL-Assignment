@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo STARTING TEST EXECUTION
echo ============================================================
echo.

cd /d "%~dp0"

rem Run test and filter output to show only test status
mvn test -Dbrowser=chrome -Dheadless=true 2>&1 | findstr /R /C:"STARTING" /C:"Running:" /C:"PASSED:" /C:"FAILED:" /C:"SKIPPED:" /C:"TEST SUITE" /C:"Total Tests" /C:"Passed:" /C:"Failed:" /C:"Skipped:" /C:"COMPLETED"

echo.
echo ============================================================
echo TEST EXECUTION COMPLETED
echo ============================================================
pause