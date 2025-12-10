# Clean Test Runner Script
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "🚀 STARTING TEST SUITE" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan

# Run tests with minimal output
mvn test $args -q

Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "🏁 TEST SUITE COMPLETED" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan

# Keep window open for review
Read-Host "Press Enter to exit..."