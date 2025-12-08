# Clean Test Runner Script
# Shows only test execution status without logs

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "STARTING TEST EXECUTION" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan

Set-Location $PSScriptRoot

# Run Maven and capture output, filtering out unwanted lines
$testOutput = mvn test -Dbrowser=chrome -Dheadless=true 2>&1 | Where-Object { 
    $_ -notmatch "WARNING" -and 
    $_ -notmatch "SLF4J" -and 
    $_ -notmatch "INFO.*Scanning" -and
    $_ -notmatch "INFO.*Building" -and
    $_ -notmatch "INFO.*---" -and
    $_ -notmatch "INFO.*Running" -and
    $_ -notmatch "INFO.*Tests run" -and
    $_ -notmatch "INFO.*BUILD" -and
    $_ -notmatch "INFO.*Total time" -and
    $_ -notmatch "INFO.*Finished at"
}

# Display filtered output
$testOutput | ForEach-Object {
    if ($_ -match "Running:") {
        Write-Host $_ -ForegroundColor Yellow
    } elseif ($_ -match "Passed:") {
        Write-Host $_ -ForegroundColor Green
    } elseif ($_ -match "Failed:") {
        Write-Host $_ -ForegroundColor Red
    } elseif ($_ -match "Skipped:") {
        Write-Host $_ -ForegroundColor Yellow
    } elseif ($_ -match "TEST SUITE") {
        Write-Host $_ -ForegroundColor Cyan
    } elseif ($_ -match "Total Tests") {
        Write-Host $_ -ForegroundColor White
    } elseif ($_ -match "ALL TESTS PASSED") {
        Write-Host $_ -ForegroundColor Green
    } elseif ($_ -match "SOME TESTS FAILED") {
        Write-Host $_ -ForegroundColor Red
    } else {
        Write-Host $_
    }
}

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "TEST EXECUTION COMPLETED" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Cyan