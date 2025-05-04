package huiyu.stepdefinitions_login_ds;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class login_ds {
    private WebDriver driver;
    private WebDriverWait wait;
    
    @Given("I open the login page {string}")
    public void i_open_the_login_page(String url) {
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        // driver = new ChromeDriver();
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get(url);
    }
    
    @Then("I should see Username APIKey and Login Button on the login page")
    public void i_should_see_username_apikey_and_login_button_on_the_login_page() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login")));    

        Assert.assertTrue(driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.cssSelector("#welcome-login")).isDisplayed());
    }
    
    @When("I enter username {string} and APIKey {string}")
    public void i_enter_username_and_apikey(String username, String apiKey) {
        driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']")).sendKeys(username);
        driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']")).sendKeys(apiKey);
    }
    
    @And("I click the login button")
    public void i_click_the_login_button() {
        driver.findElement(By.cssSelector("#welcome-login")).click();
    }
    
    @Then("I should see the terms and conditions popup")
    public void i_should_see_the_terms_and_conditions_popup() {
        Assert.assertTrue(driver.findElement(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")).isDisplayed());
    }
    
    @When("I move to the accept button to ensure it is visible")
    public void i_move_to_the_accept_button_to_ensure_it_is_visible() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }
    
    @And("I click the accept button")
    public void i_click_the_accept_button() {
        driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb")).click();
    }
    
    @Then("I should be logged in and see my profile avatar")
    public void i_should_be_logged_in_and_see_my_profile_avatar() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header")));
        Assert.assertTrue(driver.findElement(By.cssSelector("groupui-avatar")).isDisplayed());
    }
}