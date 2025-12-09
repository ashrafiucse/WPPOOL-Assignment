Write-Host "=================================================="
Write-Host "? STARTING COMPLETE TEST SUITE"
Write-Host "=================================================="
Write-Host ""
Write-Host "Running all WordPress FlexTable tests in headless mode..."
Write-Host ""

mvn clean test -Dbrowser=chrome -Dheadless=true -Dsurefire.suiteXmlFiles=testng-comprehensive.xml

Write-Host ""
Write-Host "=================================================="
Write-Host "? TEST SUITE COMPLETED"
Write-Host "=================================================="