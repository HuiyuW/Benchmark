package huiyu.stepdefinitions_tenant_blank_name_testUI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import huiyu.utils.SimplifiedUiTraceListener;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class TenantSteps extends BaseTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private SimplifiedUiTraceListener traceListener;
    private WebDriver originalDriver;
    
    // Define a formatter for the run directory name
    private static final DateTimeFormatter RUN_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"); 

    @Before
    public void setUp() {
        System.out.println("===== TenantSteps: @Before setUp method CALLED (tenant_blank_red_error) ====="); 
        WebDriverManager.firefoxdriver().setup();
        originalDriver = new FirefoxDriver(); 
        System.out.println("===== TenantSteps: Original FirefoxDriver created: " + originalDriver.hashCode() + " =====");

        // --- NEW: Generate unique run directory path ---
        String timestamp = LocalDateTime.now().format(RUN_DIR_FORMATTER);
        // You can include test name here if you have access to it easily, e.g., from system properties or Cucumber hooks
        String testName = "tenant_blank_red_error"; // Replace with actual test name if possible
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
    @When("I select language {string}")
    public void selectLanguage(String lang) {
        WebElement sel = wait.until(
          ExpectedConditions.elementToBeClickable(By.id("changeLanguage"))
        );
        new Select(sel).selectByValue(lang);
    }

    @And("I navigate to the Tenants page")
    public void navigateToTenantsPage() {
        WebElement tenantsNav = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-top-navigation-item[routerlink='tenant']")
          )
        );
        tenantsNav.click();

        /*
        // ===== 方案 B: 先展开下拉，再点击菜单项 =====
        // 1) 点击 Administration 下拉
        WebElement adminBtn = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-top-navigation-item.dropdown")
          )
        );
        adminBtn.click();

        // 2) 点击 Tenants
        WebElement tenantsLink = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.xpath("//a[normalize-space(.)='Tenants']")
          )
        );
        tenantsLink.click();
        */
    }

    @And("I click the new tenant button")
    public void clickNewTenant() {
        // ↓↓↓ 在点击按钮前，先把鼠标移到一个空白区域 ↓↓↓
        WebElement body = driver.findElement(By.tagName("body"));
        new Actions(driver)
            .moveToElement(body, 10, 10)
            .perform();
        wait.until(
          ExpectedConditions.elementToBeClickable(By.id("btn-new-tenant"))
        ).click();
    }

    @And("I enter tenant name {string}")
    public void enterTenantName(String name) {
        // 直接定位到自定义组件，再 sendKeys
        WebElement tenantInputComp = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-input#input-add-tenant-name")
          )
        );
        tenantInputComp.sendKeys(name);
    }

    @And("I click the save tenant button")
    public void clickSaveTenant() {
        wait.until(
          ExpectedConditions.elementToBeClickable(By.id("btn-add-tenant"))
        ).click();
    }

    @Then("I should see tenant {string} in the list")
    public void verifyTenantInList(String name) {
        List<WebElement> cells = wait.until(
          ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.cssSelector("groupui-table-cell")
          )
        );
        boolean found = cells.stream()
          .anyMatch(c -> name.equals(c.getText().trim()));
        assertTrue(found, "Expected to find tenant '" + name + "' in list");
    }

    @When("I click on the tenant ID for {string}")
    public void clickTenantIdFor(String name) {
        WebElement idCell = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.xpath("//groupui-table-cell[text()='" + name + "']"
                     + "/following-sibling::groupui-table-cell")
          )
        );
        idCell.click();
    }

    @Then("I should be on the tenant details page for that tenant")
    public void verifyOnTenantDetailsPage() {
        String url = driver.getCurrentUrl();
        assertTrue(url.matches(".*/[0-9a-fA-F\\-]+$"),
                   "URL should end with a UUID tenant ID but was: " + url);
    }

    @Then("the save tenant button should be disabled")
    public void saveTenantButtonShouldBeDisabled() {
        WebElement saveBtn = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("btn-add-tenant"))
        );
        // 方法 A：检查 disabled 属性是否存在
        String disabledAttr = saveBtn.getAttribute("disabled");
        assertNotNull(disabledAttr, "按钮应有 disabled 属性，实际为: " + disabledAttr);

        // 方法 B：检查 ng-reflect-disabled="true"
        String reflect = saveBtn.getAttribute("ng-reflect-disabled");
        assertEquals("true", reflect, "ng-reflect-disabled 属性应为 true");
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
  //   @And("I click the tenant name input field")
  //   public void focusOnTenantNameInput() {
  //     // 直接点击外层组件，触发内部 input 的 focus/blur 行为
  //     WebElement comp = wait.until(
  //       ExpectedConditions.presenceOfElementLocated(
  //         By.cssSelector("groupui-input#input-add-tenant-name")
  //       )
  //     );
  //     js.executeScript("arguments[0].scrollIntoView(true);", comp);
  //     js.executeScript("arguments[0].click();", comp);
  // }

    @And("I click the tenant name input field")//没用到。
    public void focusOnTenantNameInput() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1) 定位到自定义组件宿主
        WebElement host = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("groupui-input#input-add-tenant-name")
            )
        );

        // 2) 聚焦并发送一个空格，再删除，再失焦，触发 focus/blur 验证
        host.sendKeys(" ");                  // 聚焦并插入一个空格
        host.sendKeys(Keys.BACK_SPACE);      // 删除刚才的空格
    }

    @And("I clear the tenant name input field")
    public void clearTenantNameInput() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement host = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("groupui-input#input-add-tenant-name")
            )
        );
        // 全选、删除
        host.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
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
