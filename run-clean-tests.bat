@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo 🚀 STARTING TEST EXECUTION
echo ============================================================

cd /d "%~dp0"

rem Run Maven with minimal output and suppress all warnings/errors
mvn test -Dbrowser=chrome -Dheadless=true -q --batch-mode --quiet 2>nul

if %ERRORLEVEL% EQU 0 (
    echo ============================================================
    echo ✅ ALL TESTS COMPLETED SUCCESSFULLY
    echo ============================================================
) else (
    echo ============================================================
    echo ❌ SOME TESTS FAILED
    echo ============================================================
)

pause