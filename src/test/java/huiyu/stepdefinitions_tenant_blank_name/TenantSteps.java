package huiyu.stepdefinitions_tenant_blank_name;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import huiyu.DriverManager;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TenantSteps extends BaseTest {
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @Before
    public void init() {
        this.driver = DriverManager.getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js     = (JavascriptExecutor) driver;
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

}
