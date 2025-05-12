package huiyu.Basic_UI_Button_Clicks_Analytics_navigation;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Basic_UI_Button_Clicks_Analytics_navigation {

    private WebDriver driver;
    private WebDriverWait wait;

    
    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get(url);
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 加 wait
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

    @And("I click the login button")
    public void iClickTheLoginButton() {
        driver.findElement(By.cssSelector("#welcome-login")).click();
    }

    @Then("I should see the terms and conditions popup")
    public void iTermAndConditionsPopupIsDisplayed() {
        WebElement termsAndConditions = driver.findElement(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]"));
        assert termsAndConditions.isDisplayed();
    }

    @When("I move to the accept button to ensure it is visible")
    public void iMoveToTheAcceptButton() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        // ((org.openqa.selenium.interactions.Actions) driver).moveToElement(acceptButton);自己生成的错误
        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }

    @And("I click the accept button")
    public void iClickTheAcceptButton() {
        driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb")).click();
    }

    @When("I click the {string} nav item")
    public void clickNavItem(String name) {
        // locate by text, wait until clickable, then click
        By navLocator = By.cssSelector(
          "#app-main-navigation > groupui-top-navigation > groupui-top-navigation-item:nth-child(4)"
        );
        WebElement navItem = wait.until(
          ExpectedConditions.elementToBeClickable(navLocator)
        );
        navItem.click();
    }

    @Then("the {string} nav item should be marked active")
    public void navItemShouldBeActive(String name) {
        WebElement navItem = driver.findElement(
          By.xpath("//groupui-top-navigation-item[normalize-space(text())='" + name + "']")
        );
        // the presence of the 'active' attribute indicates it’s selected
        String activeAttr = navItem.getAttribute("active");
        assertNotNull(activeAttr, "Expected the " + name + " nav item to have an 'active' attribute");
    }

    @Then("I should see the page title {string}")
    public void shouldSeePageTitle(String title) {
        // wait for the headline with exact text body > app-root > app-dashboard-overview > groupui-headline
        By titleLocator = By.xpath(
          "//groupui-headline[normalize-space(text())='" + title + "']"
        );
        WebElement pageTitle = wait.until(
          ExpectedConditions.visibilityOfElementLocated(titleLocator)
        );
        assertEquals(title, pageTitle.getText());
    }
}