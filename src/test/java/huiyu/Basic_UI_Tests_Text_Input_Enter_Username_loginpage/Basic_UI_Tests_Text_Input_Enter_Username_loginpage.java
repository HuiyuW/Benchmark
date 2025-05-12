package huiyu.Basic_UI_Tests_Text_Input_Enter_Username_loginpage;

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

public class Basic_UI_Tests_Text_Input_Enter_Username_loginpage {

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
    @Then("the username input field should have value {string}")
    public void the_username_input_field_should_have_value(String expected) {
        WebElement input = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        String actual = input.getAttribute("value");
        assertEquals(expected, actual, "用户名输入框的 value 应为 " + expected);
    }
}