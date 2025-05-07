package huiyu.stepdefinitions_user_deactivate_login_fail_llama_testUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.utils.SimplifiedUiTraceListener;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
public class user_deactivate_login_fail_llama {

    private WebDriver driver;
    private WebDriverWait wait;
    private SimplifiedUiTraceListener traceListener;
    private WebDriver originalDriver;
    
    // Define a formatter for the run directory name
    private static final DateTimeFormatter RUN_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"); 

    @Before
    public void setUp() {
        System.out.println("===== TenantSteps: @Before setUp method CALLED (deactivate User) ====="); 
        WebDriverManager.firefoxdriver().setup();
        originalDriver = new FirefoxDriver(); 
        System.out.println("===== TenantSteps: Original FirefoxDriver created: " + originalDriver.hashCode() + " =====");

        // --- NEW: Generate unique run directory path ---
        String timestamp = LocalDateTime.now().format(RUN_DIR_FORMATTER);
        // You can include test name here if you have access to it easily, e.g., from system properties or Cucumber hooks
        String testName = "deactivateUser"; // Replace with actual test name if possible
        String runDirName = testName + "_" + timestamp;
        // Save under target/ui-benchmarks/run_name
        String runBasePath = Paths.get("target", "ui-benchmarks", runDirName).toString(); 
        
        // Create the run directory
        try {
             Files.createDirectories(Paths.get(runBasePath));
             System.out.println("===== TenantSteps: Created run directory: " + runBasePath + " =====");
        } catch (IOException e) {
            System.err.println("===== TenantSteps: FAILED to create run directory: " + runBasePath + ". Error: " + e.getMessage() + " =====");
            // Decide how to handle this error - maybe fall back to default directory or stop test
            // For now, let's re-throw or handle gracefully if directory is crucial
             throw new RuntimeException("Failed to create run directory", e); // Stop test if directory creation fails
        }
        // --- END NEW ---

        // Use the generated run directory path
        String traceFileName = "trace.ui.trace.json"; // Simple name within the run directory
        String traceFilePath = Paths.get(runBasePath, traceFileName).toString(); 

        traceListener = new SimplifiedUiTraceListener(traceFilePath, originalDriver); // Pass originalDriver
        System.out.println("===== TenantSteps: SimplifiedUiTraceListener v4 instance created =====");

        driver = new EventFiringDecorator<>(traceListener).decorate(originalDriver);
        System.out.println("===== TenantSteps: Decorated driver created: " + driver.hashCode() + " =====");
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        System.out.println("===== TenantSteps: setUp complete. Driver is decorated. =====");
    }
    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        // WebDriverManager.firefoxdriver().setup();
        // driver = new FirefoxDriver();
        driver.get(url);
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login")));  
        WebElement usernameField = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        WebElement apiKeyField = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        WebElement loginButton = driver.findElement(By.cssSelector("#welcome-login"));

        assert usernameField.isDisplayed();
        assert apiKeyField.isDisplayed();
        assert loginButton.isDisplayed();
    }

    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndAPIKey(String username, String apiKey) {
        driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']")).sendKeys(username);
        driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']")).sendKeys(apiKey);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        driver.findElement(By.cssSelector("#welcome-login")).click();
    }

    @Then("I should see the terms and conditions popup")
    public void iTermAndConditionsPopupIsDisplayed() {
        WebElement termsAndConditions = driver.findElement(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]"));
        assert termsAndConditions.isDisplayed();
    }

    @When("I move to the accept button to ensure it is visible")
    public void iMoveToTheAcceptButtonToEnsureItIsVisible() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        // ((org.openqa.selenium.interactions.Actions) driver).moveToElement(acceptButton);自己生成的错误
        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }

    @And("I click the accept button")
    public void iClickTheAcceptButton() {
        driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb")).click();
    }

    @Then("I should be logged in and see my profile avatar")
    public void iTShouldBeLoggedInAndSeeMyProfileAvatar() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header")));
        WebElement profileAvatar = driver.findElement(By.cssSelector("groupui-avatar"));
        assert profileAvatar.isDisplayed();
    }

    @When("I switch the portal language to {string}")
    public void iSwitchThePortalLanguageTo(String language) {
        // Assuming the language dropdown is a select element with id "language-select"
        WebElement languageSelect = driver.findElement(By.id("changeLanguage"));
        new Select(languageSelect).selectByValue(language);
    }

    @When("I open the Administration dropdown in the top navigation")
    public void iOpenTheAdministrationDropdownInTheTopNavigation() {
        WebElement administrationDropdown = driver.findElement(By.cssSelector("groupui-top-navigation-item.dropdown[routerlink='tenant']"));
        // ((org.openqa.selenium.interactions.Actions) driver).moveToElement(administrationDropdown);
        administrationDropdown.click();
    }

    @And("I select Users from the Administration dropdown")
    public void iSelectUsersFromTheAdministrationDropdown() {
        WebElement usersOption = driver.findElement(By.cssSelector("groupui-top-navigation-item[routerlink='user']"));
        usersOption.click();
    }

    @And("I locate the user {string} in the list")
    public void iLocateTheUserInTheList(String username) {
        wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")
          )
        );
    }

    @And("I deactivate the user {string}")
    public void iDeactivateTheUser(String username) {
        WebElement deactivateButton = driver.findElement(By.cssSelector("#user-list-deactivate-3efd7664-632c-11ee-8c99-0242ac12000"));
        deactivateButton.click();
    }

    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }

    @And("I wait until the deactivation-confirmation toast disappears")
    public void iWaitUntilTheDeactivationConfirmationToastDisappears() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector("div.toast-message[role='alert'][aria-label^='Deactivate user']")
        ));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }

    @And("I click the profile avatar")
    public void iClickTheProfileAvatar() {
        driver.findElement(By.cssSelector("groupui-avatar")).click();
    }

    @And("I click the logout button")
    public void iClickTheLogoutButton() {
        WebElement logoutButton = driver.findElement(By.cssSelector("#app-logout"));
        logoutButton.click();
    }
    @After
    public void tearDown() {
        System.out.println("===== TenantSteps: @After tearDown START ====="); 
        if (traceListener != null) {
            traceListener.saveTrace();
        } else {
            System.err.println("===== TenantSteps: @After tearDown - traceListener is NULL! =====");
        }
        if (driver != null) {
            driver.quit();
        } else {
            System.err.println("===== TenantSteps: @After tearDown - driver is NULL! =====");
        }
        System.out.println("===== TenantSteps: @After tearDown END =====");
    }

}