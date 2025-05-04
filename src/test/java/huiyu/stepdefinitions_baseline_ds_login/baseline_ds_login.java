package huiyu.stepdefinitions_baseline_ds_login;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class baseline_ds_login extends BaseTest {

    private WebDriverWait wait;

    @Before
    public void init() {
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I open the login page at {string}")
    public void iOpenTheLoginPageAt(String url) {
        driver.get(url);
    }
    
    @Then("I should see the Username field, APIKey field, and a Login button")
    public void iShouldSeeTheUsernameFieldAPIKeyFieldAndALoginButton() {
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        assertNotNull(username);
        
        WebElement apikey = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        assertNotNull(apikey);
        
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#welcome-login")));
        assertNotNull(loginButton);
    }
    
    @When("I enter the username {string} and the APIKey {string}")
    public void iEnterTheUsernameAndTheAPIKey(String username, String apikey) {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        usernameField.sendKeys(username);
        
        WebElement apikeyField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        apikeyField.sendKeys(apikey);
    }
    
    @And("I click the login button")
    public void iClickTheLoginButton() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#welcome-login")));
        loginButton.click();
    }
    
    @Then("I should see the Terms and Conditions popup")
    public void iShouldSeeTheTermsAndConditionsPopup() {
        WebElement termsPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")));
        assertTrue(termsPopup.isDisplayed());
    }
    
    @When("I scroll or move to the {string} button to ensure it is visible")
    public void iScrollOrMoveToTheButtonToEnsureItIsVisible(String buttonText) {
        WebElement acceptButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-button#welcome-agree-agb")));
        
        Actions action = new Actions(driver);
        action.moveToElement(acceptButton).perform();
    }
    
    @And("I click the {string} button")
    public void iClickTheButton(String buttonText) {
        WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("groupui-button#welcome-agree-agb")));
        acceptButton.click();
    }
    
    @Then("I should be successfully logged in and see my profile avatar")
    public void iShouldBeSuccessfullyLoggedInAndSeeMyProfileAvatar() {
        WebElement profileAvatar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
        assertNotNull(profileAvatar);
    }

}
