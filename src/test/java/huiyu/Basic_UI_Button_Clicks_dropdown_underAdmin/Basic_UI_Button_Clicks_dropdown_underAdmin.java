package huiyu.Basic_UI_Button_Clicks_dropdown_underAdmin;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

public class Basic_UI_Button_Clicks_dropdown_underAdmin {

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
    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
    @When("I hover over the Administration menu")
    public void hoverOverAdmin() {

        // 找到 “Administration” 顶部菜单项
        WebElement adminMenu = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("groupui-top-navigation-item.dropdown[routerlink='tenant']")
            )
        );
        // 悬停展开下拉
        new Actions(driver)
        .moveToElement(adminMenu)
        .perform();
    }

    @Then("the Administration dropdown should be visible")
    public void assertDropdownVisible() {
        // 下拉菜单 <ul class="dropdown-menu"> 应可见
        WebElement dropdown = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.dropdown > ul.dropdown-menu")
            )
        );
        assertTrue(dropdown.isDisplayed(), "Administration dropdown must be visible on hover");
    }

    @When("I click the {string} option in the Administration dropdown")
    public void clickDropdownOption(String optionText) {
        // 悬停后再定位并点击相应项
        String routerLink = optionText
            .toLowerCase()                // Users → users
            .replace(" ", "-")            // balance charge → balance-charge
            .replaceAll("[^a-z0-9\\-]", ""); // 只保留 小写字母、数字、连字符

        // 2) 动态拼接 CSS Selector
        String css = String.format("groupui-top-navigation-item[routerlink='%s']", routerLink);
        By Item = By.cssSelector(css);
        WebElement Option = wait.until(ExpectedConditions.visibilityOfElementLocated(Item));
        assertTrue(Option.isDisplayed(), "The 'Users' option should be visible in the dropdown");
        Option.click();
    }

    @Then("I should be navigated to the {string} page")
    public void verifyNavigation(String page) {
        String page_link = page
            .toLowerCase()                // Users → users
            .replace(" ", "-")            // balance charge → balance-charge
            .replaceAll("[^a-z0-9\\-]", ""); // 只保留 小写字母、数字、连字符

        // 根据 routerLink 验证 URL
        String expectedFragment = "/" + page_link;
        wait.until(d -> d.getCurrentUrl().contains(expectedFragment));
        assertTrue(driver.getCurrentUrl().contains(expectedFragment),
                   "URL should contain " + expectedFragment);
    }
}