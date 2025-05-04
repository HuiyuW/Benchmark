import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class user_deactivate_login_fail_ds {
    private WebDriver driver = new ChromeDriver();
    
    @Given("I open the login page {string}")
    public void i_open_the_login_page(String url) {
        driver.get(url);
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void i_should_see_username_apikey_and_login_button_on_the_login_page() {
        Assert.assertTrue(driver.findElement(By.id("usernameInput")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("apiKeyInput")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.id("loginButton")).isDisplayed());
    }
    
    // Add other steps here...
}