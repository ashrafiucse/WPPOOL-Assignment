@echo off
echo ==================================================
echo ? STARTING COMPLETE TEST SUITE
echo ==================================================
echo.
echo Running all WordPress FlexTable tests in headless mode...
echo.

mvn clean test -Dbrowser=chrome -Dheadless=true -Dsurefire.suiteXmlFiles=testng-comprehensive.xml

echo.
echo ==================================================
echo ? TEST SUITE COMPLETED
echo ==================================================
pause