// src/test/java/huiyu/stepdefinitions_logout/LogoutSteps.java
package huiyu.stepdefinitions_logout;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import huiyu.BaseTest;
import huiyu.DriverManager;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LogoutSteps extends BaseTest {
    private WebDriverWait wait;

    @Before
    public void reuseDriver() {
        // 拿到之前登录时创建的 driver
        this.driver = DriverManager.getDriver();
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
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

    @After
    public void tearDownDriver() {
        // 注销后退出浏览器并清空持有器
        tearDown();
        DriverManager.clear();
    }
}
