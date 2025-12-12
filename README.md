# WPPOOL Selenium Automation Testing

A comprehensive Selenium WebDriver automation framework for testing WPPOOL web application with support for sequential and parallel test execution across multiple browsers and beautiful Allure reporting.

## 🚀 Key Features

- **Multiple Browser Support** - Chrome, Firefox, Edge with automatic driver management
- **Headless & Headed Modes** - Run tests with or without browser GUI
- **Sequential & Parallel Execution** - Choose between stable sequential or fast parallel execution
- **Page Object Model** - Clean, maintainable code structure
- **Environment Configuration** - Centralized configuration via .env file
- **Allure Reporting** - Beautiful, interactive test reports with screenshots and logs
- **Explicit Waits Only** - No flaky tests, reliable execution
- **Thread-Safe Architecture** - Safe parallel execution with isolated driver instances
- **GitHub Actions CI/CD** - Automated testing on every push and pull request

---

## ⚙️ Prerequisites & Setup

### System Requirements
- **Java**: Version 8 or higher (17+ recommended)
- **Maven**: Version 3.6+ 
- **RAM**: Minimum 8GB (16GB recommended for parallel execution)

### Software Installation

#### Install Browsers (Choose at least one)
```bash
# Chrome (Recommended)
# Download from: https://www.google.com/chrome/

# Firefox
# Download from: https://www.mozilla.org/firefox/download/thanks/

# Microsoft Edge (Pre-installed on Windows 10+)
# Already available on most Windows systems
```

> **⚠️ Important**: The browsers listed above must be installed on your computer to run the tests. The project includes the necessary WebDrivers (chromedriver, geckodriver, etc.) in the `drivers/` folder, but you need the actual browser applications installed to execute the tests.

#### Install Java 8+
```bash
# Windows (using Chocolatey)
choco install openjdk

# macOS (using Homebrew)
brew install openjdk

# Linux (Ubuntu/Debian)
sudo apt install openjdk-8-jdk
```

#### Install Maven
```bash
# Windows (using Chocolatey)
choco install maven

# macOS (using Homebrew)
brew install maven

# Linux (Ubuntu/Debian)
sudo apt install maven
```

### Project Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/ashrafiucse/WPPOOL-Assignment.git
cd WPPOOL-Assignment
```

#### 2. Set up Environment Variables
```bash
cp .env.example .env
# Edit .env with your actual credentials
```

#### 3. Install Dependencies
```bash
mvn clean install
```

#### 4. Configure Test Data
Update the following variables in your `.env` file:
- `WP_URL`: Your WordPress admin URL
- `WP_USER`: Your WordPress admin username
- `WP_PASS`: Your WordPress admin password
- `GOOGLE_SHEET_LINK`: Link to your Google Sheet for testing

Example `.env` content:
```bash
WP_URL=https://your-site.com/wp-admin
WP_USER=admin
WP_PASS=your_secure_password
GOOGLE_SHEET_LINK=https://docs.google.com/spreadsheets/d/1234567890abcdef/edit
```

#### 4. Terminal Encoding (Windows Only)
```bash
# Command Prompt
chcp 65001

# PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

---

## 🚀 Test Execution Commands

### Sequential Execution (Recommended - Stable)

#### Headless Mode
```bash
# Chrome Sequential Headless
mvn clean test -Dbrowser=chrome -Dheadless=true

# Firefox Sequential Headless
mvn clean test -Dbrowser=firefox -Dheadless=true

# Edge Sequential Headless  
mvn clean test -Dbrowser=edge -Dheadless=true
```

#### Headed Mode (Browser Window Visible)
```bash
# Chrome Sequential Headed
mvn clean test -Dbrowser=chrome

# Firefox Sequential Headed
mvn clean test -Dbrowser=firefox

# Edge Sequential Headed
mvn clean test -Dbrowser=edge
```



### Special Commands

#### Quick Smoke Tests
```bash
mvn clean test -Psmoke -Dbrowser=chrome -Dheadless=true
```

#### Single Test Execution
```bash
# Run specific test class
mvn test -Dtest=YourTestClass -Dbrowser=chrome -Dheadless=true

# Run specific test method  
mvn test -Dtest=YourTestClass#yourTestMethod -Dbrowser=chrome
```

---

## 📊 Test Reports with Allure

### Generate and View Reports
```bash
# Run tests and generate report
mvn clean test

# Generate Allure report
mvn allure:report

# Serve report locally (opens in browser)
mvn allure:serve

# Run tests and immediately serve report
mvn clean test allure:serve
```

### Report Features
- **Interactive Dashboard** - Overview of test execution
- **Test Details** - Step-by-step execution with screenshots
- **Timeline View** - Parallel execution visualization
- **Categories** - Group tests by type (smoke, regression, etc.)
- **Environment Info** - Browser, OS, and execution details

Report will be available at `http://localhost:8080` when using `allure:serve`

### Troubleshooting Allure Reports
If `mvn allure:report` fails due to dependency issues:
- Install Allure CLI from https://github.com/allure-framework/allure2/releases
- Generate report: `allure generate target/surefire-reports --clean --output target/allure-report`
- Serve report: `allure serve target/allure-report`
- This creates the same interactive report from TestNG/surefire results.

---

## 🤖 GitHub Actions CI/CD

### Automated Testing Pipeline

The project includes GitHub Actions workflows that automatically run tests on every push and pull request:

#### Main Test Suite (`selenium-tests.yml`)
- **Triggers**: Push to main/master branches, pull requests
- **Browsers**: Chrome, Firefox, Edge (all in headless mode)
- **Reports**: Allure reports and test artifacts uploaded
- **Matrix Strategy**: Tests run in parallel across all browsers

#### Quick Feedback (Pull Requests)
- **Smoke Tests**: Fast feedback with Chrome only for pull requests
- **Parallel Execution**: Reduces CI time for faster development cycles

### Workflow Features
- ✅ **Cross-browser testing** across Chrome, Firefox, and Edge
- ✅ **Automatic browser installation** on Ubuntu runners
- ✅ **Maven caching** for faster builds
- ✅ **Test artifact uploads** for detailed analysis
- ✅ **Allure report generation** and upload
- ✅ **Failure notifications** (configurable)

### Viewing CI Results
1. Go to the **Actions** tab in your GitHub repository
2. Click on the latest workflow run
3. Download **artifacts** to view detailed test reports
4. Access **Allure reports** for interactive test results

---



## 📊 Test Coverage

### Test Statistics
- **Total Tests**: Growing test suite
  - **Login Tests**: Authentication and authorization
  - **Dashboard Tests**: Main functionality verification
  - **User Management Tests**: CRUD operations
  - **Settings Tests**: Configuration management
  - **Additional Tests**: Feature-specific test cases

### Performance
- **Sequential**: Stable execution, lower resource usage
- **Headless**: Faster execution, no GUI
- **Headed**: Visual debugging, slower execution

---

## 🏗️ Project Structure

```
WPPOOL-Assignment/
├── .idea/
│   ├── .gitignore
│   ├── encodings.xml
│   ├── misc.xml
│   └── vcs.xml
├── src/
│   └── test/
│       └── java/
│           ├── pages/
│           │   └── BasePage.java
│           └── utilities/
│               ├── DriverSetup.java
│               └── ConfigManager.java
├── .env
├── .env.example
├── .gitignore
├── pom.xml
├── testng.xml
└── README.md
```

### Key Components
- **BasePage.java** - Common page methods and utilities
- **DriverSetup.java** - WebDriver initialization and management
- **ConfigManager.java** - Environment variable management
- **testng.xml** - TestNG configuration for parallel/sequential execution
- **.env** - Centralized configuration file

---

## 🚨 Troubleshooting

### Common Issues

#### Tests Running Multiple Times
```bash
# Use sequential execution (default)
mvn clean test
```

#### Browser Driver Issues
```bash
mvn clean install -U
```

#### Memory Issues
```bash
export MAVEN_OPTS="-Xmx4g -Xms2g"
mvn clean test
```

#### For Debugging
```bash
# Run single test with browser visible
mvn test -Dtest=TestClass#testMethod -Dbrowser=chrome
```

#### Allure Report Issues
```bash
# Clean previous results
mvn clean

# Regenerate report
mvn test allure:report
```

---

## 🔧 Default Settings

- **Execution**: Sequential (stable)
- **Browser**: Chrome
- **Mode**: Headed (browser window visible)
- **Timeout**: 10 seconds for waits
- **Reports**: Allure with automatic generation
- **Configuration**: Environment variables via .env

---

## 📚 Dependencies

- **Selenium WebDriver** (4.26.0) - Web automation framework
- **TestNG** (7.10.2) - Testing framework
- **Allure TestNG** (2.29.1) - Test reporting
- **WebDriverManager** (6.2.0) - Browser driver management
- **JavaFaker** (1.0.2) - Test data generation
- **Dotenv Java** (3.0.0) - Environment variable management

---

**Happy Testing! 🚀**
