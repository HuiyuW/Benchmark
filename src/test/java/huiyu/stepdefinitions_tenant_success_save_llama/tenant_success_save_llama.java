package huiyu.stepdefinitions_tenant_success_save_llama;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import io.github.bonigarcia.wdm.WebDriverManager;

public class tenant_success_save_llama {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
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
    // @And("I type tenant name {string}")
    // public void enterTenantName(String name) {
    //     // 直接定位到自定义组件，再 sendKeys
    //     WebElement tenantInputComp = wait.until(
    //       ExpectedConditions.elementToBeClickable(
    //         By.cssSelector("groupui-input#input-add-tenant-name")
    //       )
    //     );
    //     tenantInputComp.sendKeys(name);
    // }


    @And("I click the Save Tenant button")
    public void iClickTheSaveTenantButton() {
        WebElement saveTenantButton = driver.findElement(By.cssSelector("#btn-add-tenant"));
        saveTenantButton.click();
    }




    // @Then("the tenant {string} appears in the list")
    // public void theTenantAppearsInTheList(String tenantName) {
    //     // Assuming the tenant list is a table with id "tenant-list"
    //     WebElement tenantList = driver.findElement(By.cssSelector("groupui-table-cell"));
    //     assert tenantList.isDisplayed();
    //     // Check if the tenant name exists in the list
    //     boolean found = false;
    //     for (WebElement row : tenantList.findElements(By.tagName("tr"))) {
    //         if (row.getText().contains(tenantName)) {
    //             found = true;
    //             break;
    //         }
    //     }
    //     assert found;
    // }

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
        driver.quit();
    }
}