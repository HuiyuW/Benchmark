// src/test/java/huiyu/pages/TermsPage.java
package huiyu.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TermsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private By acceptButton = By.cssSelector("groupui-button#welcome-agree-agb");

    public TermsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void acceptIfAppears() {
        try {
            wait.until(
              ExpectedConditions.visibilityOfElementLocated(acceptButton)
            ).click();
        } catch (Exception e) {
            // 未出现弹窗，忽略
        }
    }
}
