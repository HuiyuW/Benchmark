package huiyu.Basic_UI_Tests_Text_Input_Enter_APIKey_loginpage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Basic_UI_Tests_Text_Input_Enter_APIKey_loginpage {

    private WebDriver driver;
    private WebDriverWait wait;

    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get(url);
    }

    @When("I enter APIKey {string} in the APIKey input field")
    public void 在用户名输入框输入APIKey(String APIKey) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        WebElement input = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        // input.clear();
        input.sendKeys(APIKey);
    }

    @Then("the APIKey input field should have value {string}")
    public void 验证APIKey输入框值(String expected) {
        WebElement input = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        String actual = input.getAttribute("value");
        assertEquals(expected, actual, "APIKey输入框的 value 应为 " + expected);
    }
}