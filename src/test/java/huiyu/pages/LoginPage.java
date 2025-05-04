// src/test/java/huiyu/pages/LoginPage.java
package huiyu.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private By usernameField = By.cssSelector("groupui-input[id='welcome-user-name']");
    private By apiKeyField   = By.cssSelector("groupui-input[id='welcome-api-key']");
    private By loginButton   = By.cssSelector("#welcome-login");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
    }

    public void login(String username, String apiKey) {
        WebElement u = wait.until(
            ExpectedConditions.visibilityOfElementLocated(usernameField)
        );
        u.sendKeys(username);
        WebElement k = wait.until(
            ExpectedConditions.visibilityOfElementLocated(apiKeyField)
        );
        k.sendKeys(apiKey);
        wait.until(
            ExpectedConditions.elementToBeClickable(loginButton)
        ).click();
    }
}
