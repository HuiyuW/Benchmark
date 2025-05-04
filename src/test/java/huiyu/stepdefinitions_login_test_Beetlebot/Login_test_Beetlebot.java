package huiyu.stepdefinitions_login_test_Beetlebot;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

public class Login_test_Beetlebot extends BaseTest {

    private WebDriverWait wait;

    @Before
    public void init() {
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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

}
