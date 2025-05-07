package huiyu.stepdefinitions_login_testUI2;

import java.nio.file.Paths; // Needed for UiTraceListener save
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.utils.SimplifiedUiTraceListener;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given; // Import the simplified listener
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;


// public class Login extends BaseTest {
public class Login {
    // private WebDriverWait wait;

    // @Before
    // public void init() {
    //     setUp();
    //     wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    // }

    private WebDriver driver; // This will be the EventFiringDecorator-wrapped driver
    private WebDriverWait wait;
    private SimplifiedUiTraceListener traceListener; // Using the simplified listener
    private WebDriver originalDriver; // Keep a reference to the raw driver for the listener

    @Before
    public void setUp() {
        System.out.println("===== LoginSteps: @Before setUp method CALLED (Login2.java) =====");
        WebDriverManager.firefoxdriver().setup();
        originalDriver = new FirefoxDriver(); // Create the raw driver first

        // Setup UiTraceListener
        String traceFileName = "trace_login_simplified_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".ui.trace.json";
        String traceFilePath = Paths.get("target", "ui-traces", traceFileName).toString();
        try {
            Paths.get("target", "ui-traces").toFile().mkdirs();
        } catch (Exception e) {
            System.err.println("Could not create directory for traces: " + e.getMessage());
        }
        traceListener = new SimplifiedUiTraceListener(traceFilePath, originalDriver); // Pass originalDriver

        // Decorate the driver
        driver = new EventFiringDecorator<>(traceListener).decorate(originalDriver);
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Use decorated driver for wait
        System.out.println("<<<<< Login Steps: setUp complete. Driver is decorated. >>>>>");
    }

    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        System.out.println("<<<<< Login Steps: Opening page: " + url + " >>>>>");
        driver.get(url); // This should trigger afterGet or afterTo in the listener
    }
    
    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        System.out.println("<<<<< Login Steps: Verifying login page elements visibility >>>>>");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login"))); // Assumes #welcome-login is the groupui-button
        System.out.println("<<<<< Login Steps: Login page elements verified. >>>>>");
    }

    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndAPIKey(String username, String apiKey) {
        System.out.println("<<<<< Login Steps: Entering username: " + username + " >>>>>");
        WebElement usernameInputComponent = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        usernameInputComponent.sendKeys(username); // Should trigger afterSendKeys

        System.out.println("<<<<< Login Steps: Entering APIKey >>>>>");
        WebElement apiKeyInputComponent = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        apiKeyInputComponent.sendKeys(apiKey); // Should trigger afterSendKeys
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        System.out.println("<<<<< Login Steps: Clicking login button >>>>>");
        // Assuming #welcome-login is the ID of the <groupui-button> component
        WebElement loginButtonComponent = driver.findElement(By.cssSelector("#welcome-login"));
        loginButtonComponent.click(); // Should trigger afterClick
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
    @After
    public void tearDown() {
        System.out.println("<<<<< Login Steps: tearDown starting >>>>>");
        if (traceListener != null) {
            traceListener.saveTrace();
        }
        if (driver != null) {
            driver.quit();
        }
        System.out.println("<<<<< Login Steps: tearDown complete >>>>>");
    }
}
