package huiyu.stepdefinitions_user_deactivate_activate_admin;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
public class user_edit_admin {

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

    @When("I toggle the admin checkbox for user {string}")
    public void toggleAdminCheckboxForUser(String username) {
        // 1) 找到那格显示用户名的 cell
        WebElement userCell = wait.until(ExpectedConditions
        .visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")));

        // 2) 读出它的 row 属性
        String row = userCell.getAttribute("row");

        // 3) 再用这个 row 去定位到同一行里的 checkbox（纯 CSS）
        WebElement checkbox = wait.until(ExpectedConditions
        .elementToBeClickable(By.cssSelector(
            "groupui-table-cell[row='" + row + "'] groupui-checkbox")));

        // 4) 点击
        checkbox.click();
    }

    @Then("the admin checkbox for user {string} should not be selected")
    public void verifyAdminCheckboxForUser(String username) {
        WebElement userCell = wait.until(ExpectedConditions
        .visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")));
        String row = userCell.getAttribute("row");

        WebElement checkbox = wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.cssSelector(
            "groupui-table-cell[row='" + row + "'] groupui-checkbox")));

        // 断言它的 ng-reflect-model 属性已经变成 "true"
        String model = checkbox.getAttribute("ng-reflect-model");
        assertEquals("false", model,
        "期望 '" + username + "' 行的 checkbox 被选中，但 ng-reflect-model="+model);
    }


    @Then("the admin checkbox for user {string} should be selected")
    public void verifyAdminCheckboxForUser2(String username) {
        WebElement userCell = wait.until(ExpectedConditions
        .visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")));
        String row = userCell.getAttribute("row");

        WebElement checkbox = wait.until(ExpectedConditions
        .visibilityOfElementLocated(By.cssSelector(
            "groupui-table-cell[row='" + row + "'] groupui-checkbox")));

        String model = checkbox.getAttribute("ng-reflect-model");
        assertEquals("true", model,
        "期望 '" + username + "' 行的 checkbox 被选中，但 ng-reflect-model="+model);
    }    


    @Then("I click the Edit button for user {string}")
    public void clickEditButtonForUser(String username) {
        // 1) 找到显示用户名的 cell，读出它的 row
        WebElement userCell = wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")));
        String row = userCell.getAttribute("row");
    
        // 2) 在同一行的 options 单元格里，直接定位 id 以 "user-list-edit" 开头的 Edit 按钮
        WebElement editBtn = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-table-cell.options[row='" + row + "'] > groupui-button[id^='user-list-edit']")
          )
        );
    
        // 3) 点击
        editBtn.click();
    }
    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
    @And("I wait until the edit-confirmation toast disappears")
    public void iWaitUntilTheEditConfirmationToastDisappears() {
        // 匹配以 "user with id:" 开头并以 "is edited." 结尾的 toast
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector("div.toast-message[role='alert']" +
                           "[aria-label^='user with id:']" +
                           "[aria-label$='is edited.']")
        ));
        // 确保主界面可操作（比如头像可见）
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-avatar")
        ));
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

    @Then("I should see the {string} option under Administration")
    public void shouldSeeOptionUnderAdministration(String option) {
        // 等待对应文本的菜单项出现
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//groupui-top-navigation-item[normalize-space()='" + option + "']")));
    }

    @Then("I should not see the {string} option under Administration")
    public void shouldNotSeeOptionUnderAdministration(String option) {
        // 确保对应文本的菜单项要么不存在，要么不可见
        List<WebElement> elems = driver.findElements(
            By.xpath("//groupui-top-navigation-item[normalize-space()='" + option + "']"));
        boolean anyVisible = elems.stream().anyMatch(WebElement::isDisplayed);
        assertFalse(anyVisible, "Expected not to see '" + option + "' under Administration");
    }
}