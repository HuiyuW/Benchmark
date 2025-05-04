import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class tenant_success_save_ds {
    private WebDriver driver;
    
    @Given("I open the login page {string}")
    public void i_open_the_login_page(String url) {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get(url);
    }
    
    @Then("I should see Username APIKey and Login Button on the login page")
    public void i_should_see_username_apikey_and_login_button_on_the_login_page() {
        Assert.assertTrue(driver.findElements(By.id("usernameInput")).size() > 0);
        Assert.assertTrue(driver.findElements(By.id("apiKeyInput")).size() > 0);
        Assert.assertTrue(driver.findElements(By.id("loginButton")).size() > 0);
    }
    
    @When("I enter username {string} and APIKey {string}")
    public void i_enter_username_and_apikey(String username, String apiKey) {
        driver.findElement(By.id("usernameInput")).sendKeys(username);
        driver.findElement(By.id("apiKeyInput")).sendKeys(apiKey);
    }
    
    @And("I click the login button")
    public void i_click_the_login_button() {
        driver.findElement(By.id("loginButton")).click();
    }
    
    // Continue with other steps...
}