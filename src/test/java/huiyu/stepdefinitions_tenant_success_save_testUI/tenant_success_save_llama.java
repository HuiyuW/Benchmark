package huiyu.stepdefinitions_tenant_success_save_testUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime; // Import File
import java.time.format.DateTimeFormatter;
import java.util.List; // For more precise timestamp

import static org.junit.jupiter.api.Assertions.assertNotNull; // For formatting timestamp
import static org.junit.jupiter.api.Assertions.assertTrue;
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

public class tenant_success_save_llama {

    private WebDriver driver;
    private WebDriverWait wait;
    private SimplifiedUiTraceListener traceListener;
    private WebDriver originalDriver;
    
    // Define a formatter for the run directory name
    private static final DateTimeFormatter RUN_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"); 

    @Before
    public void setUp() {
        System.out.println("===== TenantSteps: @Before setUp method CALLED (tenant_success_save_llama) ====="); 
        WebDriverManager.firefoxdriver().setup();
        originalDriver = new FirefoxDriver(); 
        System.out.println("===== TenantSteps: Original FirefoxDriver created: " + originalDriver.hashCode() + " =====");

        // --- NEW: Generate unique run directory path ---
        String timestamp = LocalDateTime.now().format(RUN_DIR_FORMATTER);
        // You can include test name here if you have access to it easily, e.g., from system properties or Cucumber hooks
        String testName = "TenantSuccessSave"; // Replace with actual test name if possible
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
            driver.get(url);
        }
    
    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login")));
    }

    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndAPIKey(String username, String apiKey) {
        WebElement usernameInput = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        usernameInput.sendKeys(username);
        WebElement apiKeyInput = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        apiKeyInput.sendKeys(apiKey);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        WebElement loginButton = driver.findElement(By.cssSelector("#welcome-login"));
        loginButton.click();
    }

    @Then("I should see the terms and conditions popup")
    public void iShouldSeeTheTermsAndConditionsPopup() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")));
    }

    @When("I move to the accept button to ensure it is visible")
    public void iMoveToTheAcceptButtonToEnsureItIsVisible() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }

    @And("I click the accept button")
    public void iClickTheAcceptButton() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        acceptButton.click();
    }

    @Then("I should be logged in and see my profile avatar")
    public void iShouldBeLoggedInAndSeeMyProfileAvatar() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header")));
        WebElement profileAvatar = driver.findElement(By.cssSelector("groupui-avatar"));
        assertNotNull(profileAvatar);
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
        administrationDropdown.click();
    }

    @And("I select Tenants from the Administration dropdown")
    public void iSelectTenantsFromTheAdministrationDropdown() {
        // Assuming the Tenants option is a link with text "Tenants"
        WebElement tenantsLink = driver.findElement(By.cssSelector("groupui-top-navigation-item[routerlink='tenant']"));
        tenantsLink.click();
    }

    @And("I click the New Tenant button")
    public void iClickTheNewTenantButton() {
        WebElement body = driver.findElement(By.tagName("body"));
        new Actions(driver)
            .moveToElement(body, 10, 10)
            .perform();
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#loading")));
      WebElement newTenantButton = driver.findElement(By.cssSelector("#btn-new-tenant"));
      newTenantButton.click();
    }

    @And("I type tenant name {string}")
    public void iTypeTenantName(String tenantName) {
        WebElement tenantNameField = driver.findElement(By.cssSelector("groupui-input#input-add-tenant-name"));
        tenantNameField.sendKeys(tenantName);
    }



    @And("I click the Save Tenant button")
    public void iClickTheSaveTenantButton() {
        WebElement saveTenantButton = driver.findElement(By.cssSelector("#btn-add-tenant"));
        saveTenantButton.click();
    }



    @Then("the tenant {string} appears in the list")
    public void verifyTenantInList(String name) {
        boolean found = new WebDriverWait(driver, Duration.ofSeconds(10))
          .until(drv -> {
            List<WebElement> cells = drv.findElements(By.cssSelector("groupui-table-cell"));
            for (WebElement cell : cells) {
                try {
                    if (name.equals(cell.getText().trim())) {
                        return true;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    // 遇到 stale 元素，说明页面刷新了，直接 retry 整个 until
                    return false;
                }
            }
            return false;
          });
        assertTrue(found, "Expected to find tenant '" + name + "' in list");
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
