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
    public void iShouldSeeUsernameApiKeyAndLoginButtonOnTheLoginPage() {
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']"))));
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']"))));
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login"))));
    }
    
    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndApiKey(String username, String apiKey) {
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        WebElement apiKeyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        
        usernameInput.sendKeys(username);
        apiKeyInput.sendKeys(apiKey);
    }
    
    @And("I click the login button")
    public void iClickTheLoginButton() {
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#welcome-login")));
        loginBtn.click();
    }
    
    @Then("I should see the terms and conditions popup")
    public void iShouldSeeTheTermsAndConditionsPopup() {
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]"))));
    }
    
    @When("I move to the accept button to ensure it is visible")
    public void iMoveToTheAcceptButtonToEnsureItIsVisible() {
        Actions action = new Actions(driver);
        WebElement acceptBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-button#welcome-agree-agb")));
        action.moveToElement(acceptBtn).build().perform();
    }
    
    @And("I click the accept button")
    public void iClickTheAcceptButton() {
        WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("groupui-button#welcome-agree-agb")));
        acceptBtn.click();
    }
    
    @Then("I should be logged in and see my profile avatar")
    public void iShouldBeLoggedInAndSeeMyProfileAvatar() {
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header"))));
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar"))));
    }
    
    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void iWaitForToastErrorMessageToBecomeInvisibleAndProfileAvatarToBecomeVisible() {
        // Waiting until Toast title becomes invisible
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        // Waiting until Profile Avatar becomes visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
    
    @And("I click the profile avatar")
    public void iClickTheProfileAvatar() {
        WebElement profileAvatar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("groupui-avatar")));
        profileAvatar.click();
    }
    
    @And("I click the logout button")
    public void iClickTheLogoutButton() {
        WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#app-logout")));
        logoutBtn.click();
    }
    
    @Then("I should see Username APIKey and Login Button on the login page again")
    public void iShouldSeeUsernameApiKeyAndLoginButtonOnTheLoginPageAgain() {
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']"))));
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']"))));
        assertNotNull(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login"))));
    }

}