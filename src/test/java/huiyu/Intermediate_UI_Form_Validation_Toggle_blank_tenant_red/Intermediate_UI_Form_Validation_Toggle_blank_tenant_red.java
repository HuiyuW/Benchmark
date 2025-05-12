package huiyu.Intermediate_UI_Form_Validation_Toggle_blank_tenant_red;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class Intermediate_UI_Form_Validation_Toggle_blank_tenant_red {

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

    @Then("the save button should be disabled")
    public void saveButtonShouldBeDisabled() {
        wait.until(ExpectedConditions.attributeToBe(
          By.id("btn-add-tenant"),
          "ng-reflect-disabled",
          "true"
        ));
    
        WebElement loginBtn = driver.findElement(By.id("btn-add-tenant"));
        assertEquals("true",
                     loginBtn.getAttribute("ng-reflect-disabled"),
                     "ng-reflect-disabled 属性应为 true 才算 disabled");
    }

    @Then("the save button should be enabled")
    public void saveButtonShouldBeEnabled() {
        // 等待 Angular 把 ng-reflect-disabled 更新为 "false"
        wait.until(ExpectedConditions.attributeToBe(
          By.id("btn-add-tenant"),
          "ng-reflect-disabled",
          "false"
        ));
    
        WebElement loginBtn = driver.findElement(By.id("btn-add-tenant"));
        assertEquals("false",
                     loginBtn.getAttribute("ng-reflect-disabled"),
                     "ng-reflect-disabled 属性应为 false 才算 enabled");
    }

    @When("I click the tenant name input field")
    public void enterTenantName() {
        // 直接定位到自定义组件，再 sendKeys
        WebElement wrapper = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
              By.cssSelector("groupui-input#input-add-tenant-name")
            )
          );
          // 在它里面找到 label slot 并点击
          WebElement label = wrapper.findElement(
            By.cssSelector("span[slot='label']")
          );
        new Actions(driver)
        .moveToElement(label)  // 移到元素中点
        .click()                           // 单击，落在中点的空白区
        .perform();
        // tenantInputComp.click();
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
        WebElement body = driver.findElement(By.tagName("body"));
        new Actions(driver)
            .moveToElement(body, 10, 10)
            .perform();
    }
    @And("I navigate to the Tenants page")
    public void navigateToTenantsPage() {
        WebElement tenantsNav = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-top-navigation-item[routerlink='tenant']")
          )
        );

        tenantsNav.click();
        WebElement body = driver.findElement(By.tagName("body"));
        new Actions(driver)
            .moveToElement(body, 10, 10)
            .perform();
    }
    @And("I click the new tenant button")
    public void clickNewTenant() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector("groupui-loading-spinner#loading")
        ));        
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.id("btn-new-tenant")
            ));
        wait.until(
          ExpectedConditions.elementToBeClickable(By.id("btn-new-tenant"))
        ).click();
    }
    @And("I click the save tenant button")
    public void clickSaveTenant() {
        wait.until(
          ExpectedConditions.elementToBeClickable(By.id("btn-add-tenant"))
        ).click();
    }
    @Then("I should see validation error under tenant {string}")
    public void shouldSeeValidationError(String message) {
        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("groupui-input#input-add-tenant-name span.error-label")
            )
        );
        String text = error.getText().trim();
        assertEquals(message, text, "校验提示文本应完全匹配");
    }  
}