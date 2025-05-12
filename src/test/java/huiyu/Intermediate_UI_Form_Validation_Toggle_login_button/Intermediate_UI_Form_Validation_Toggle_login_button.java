package huiyu.Intermediate_UI_Form_Validation_Toggle_login_button;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Intermediate_UI_Form_Validation_Toggle_login_button {

    private WebDriver driver;
    private WebDriverWait wait;

    
    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get(url);
    }

    @When("I enter username {string} in the username input field")
    public void I_enter_username_in_the_username_input_field(String username) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        WebElement input = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        // input.clear();
        input.sendKeys(username);
    }
    @When("I enter APIKey {string} in the APIKey input field")
    public void 在用户名输入框输入APIKey(String APIKey) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        WebElement input = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        // input.clear();
        input.sendKeys(APIKey);
    }
    @Then("the login button should be disabled")
    public void loginButtonShouldBeDisabled() {
        wait.until(ExpectedConditions.attributeToBe(
          By.id("welcome-login"),
          "ng-reflect-disabled",
          "true"
        ));
    
        WebElement loginBtn = driver.findElement(By.id("welcome-login"));
        assertEquals("true",
                     loginBtn.getAttribute("ng-reflect-disabled"),
                     "ng-reflect-disabled 属性应为 true 才算 disabled");
    }

    @Then("the login button should be enabled")
    public void loginButtonShouldBeEnabled() {
        // 等待 Angular 把 ng-reflect-disabled 更新为 "false"
        wait.until(ExpectedConditions.attributeToBe(
          By.id("welcome-login"),
          "ng-reflect-disabled",
          "false"
        ));
    
        WebElement loginBtn = driver.findElement(By.id("welcome-login"));
        assertEquals("false",
                     loginBtn.getAttribute("ng-reflect-disabled"),
                     "ng-reflect-disabled 属性应为 false 才算 enabled");
    }
    @When("I clear the username field")
    public void clearUsernameField() {
        // 定位到 groupui-input 宿主元素
        WebElement wrapper = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-user-name']")
        )
        );
        // 全选然后删除（在 Windows 上通常是 Ctrl+A，Mac 上可能是 COMMAND+A）
        wrapper.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        wrapper.sendKeys(Keys.DELETE);
    }
    
}