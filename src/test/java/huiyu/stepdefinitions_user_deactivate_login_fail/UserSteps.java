// src/test/java/huiyu/stepdefinitions_logout/LogoutSteps.java
package huiyu.stepdefinitions_user_deactivate_login_fail;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import huiyu.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserSteps extends BaseTest {
    private WebDriverWait wait;

    @Before
    public void reuseDriver() {
        // 拿到之前登录时创建的 driver
        this.driver = DriverManager.getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    @When("I select language {string}")
    public void selectLanguage(String lang) {
        WebElement sel = wait.until(
          ExpectedConditions.elementToBeClickable(By.id("changeLanguage"))
        );
        new Select(sel).selectByValue(lang);
    }



    @When("I click the Users navigation")
    public void clickUsersNav() {
        // 1) 点击 Administration 展开
        WebElement adminBtn = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-top-navigation-item.dropdown[routerlink='tenant']")
          )
        );
        adminBtn.click();
    
        // 2) 点击下拉里的 Users（Light DOM <a> 标签）
        WebElement usersNav = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-top-navigation-item[routerlink='user']")
          )
        );
        usersNav.click();
    }

    @And("I see user {string}")
    public void seeUser(String username) {
        // 等待用户名单元格可见
        wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//groupui-table-cell[normalize-space()='" + username + "']")
          )
        );
    }

    @And("I deactivate user {string}")
    public void deactivateUser(String username) {
        // 2) 从该单元格开始，向下在 DOM 中查找第一个 id 以 "user-list-deactivate" 开头的按钮
        WebElement deactivateBtn = wait.until(
          ExpectedConditions.elementToBeClickable(
            By.xpath(
              "//groupui-table-cell[normalize-space()='" + username + "']"
              + "/following::groupui-button[starts-with(@id,'user-list-deactivate')][1]"
            )
          )
        );
        deactivateBtn.click();
    }

    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
    @And("I wait for deactivate confirmation toast to disappear")
    public void waitForDeactivateToast() {
        // 匹配 aria-label 以 "Deactivate user" 开头的 toast，然后等它消失
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector("div.toast-message[role='alert'][aria-label^='Deactivate user']")
        ));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
  
    @And("I click the profile avatar")
    public void clickAvatar() {
        WebElement avatar = wait.until(
          ExpectedConditions.elementToBeClickable(By.cssSelector("groupui-avatar"))
        );
        avatar.click();
    }

    @And("I click the logout button")
    public void clickLogout() {
        wait.until(
          ExpectedConditions.elementToBeClickable(By.cssSelector("#app-logout"))
        ).click();
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void verifyLoginPage() {
        assertNotNull(wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-user-name']")
          )
        ));
        assertNotNull(wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-api-key']")
          )
        ));
        assertNotNull(wait.until(
          ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#welcome-login")
          )
        ));
    }
    @When("I enter username {string} and APIKey {string}")
    public void i_enter_username_and_password(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // 等待 Username 输入框可见
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-user-name']")
        ));
        usernameInput.sendKeys(username);
    
        // 等待 Password 输入框可见
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-api-key']")
        ));
        passwordInput.sendKeys(password);
    }    
    @And("I click the login button")
    public void iClickTheLoginButton() {
        WebElement loginButton = driver.findElement(By.cssSelector("#welcome-login"));
        loginButton.click();
    }
    @After
    public void tearDownDriver() {
        // 注销后退出浏览器并清空持有器
        tearDown();
        DriverManager.clear();
    }
}
