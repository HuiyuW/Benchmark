
package huiyu.stepdefinitions_login_test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginTest_test extends BaseTest {

    private WebDriverWait wait;

    @Before
    public void init() {
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I open the login page {string}")
    public void i_open_the_login_page(String url) {
        driver.get(url);
    }


    @When("I enter username {string} and APIKey {string}")
    public void i_enter_username_and_APIKey(String username, String APIKey) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-user-name']")
        ));
        usernameInput.sendKeys(username);
    

        WebElement APIKeyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("groupui-input[id='welcome-api-key']")
        ));
        APIKeyInput.sendKeys(APIKey);
    }



    @When("I click the login button")
    public void i_click_the_login_button() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#welcome-login")));
        loginButton.click();
    }

    @Then("I should see the terms and conditions popup")
    public void i_should_see_the_terms_and_conditions_popup() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")
        ));
        assertTrue(popup.isDisplayed(), "Terms and conditions popup should be displayed.");
    }

    
    @When("I move to the accept button to ensure it is visible")
    public void i_move_to_the_accept_button_to_ensure_it_is_visible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 找到 Accept 按钮
        WebElement acceptButton = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("groupui-button#welcome-agree-agb")
        ));

        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }

    

    @When("I click the accept button")
    public void i_click_the_accept_button() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // 等待 Accept 按钮可见并可点击
        WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("groupui-button#welcome-agree-agb")
        ));
    
        // 使用 Selenium 原生方法点击按钮
        acceptButton.click();
    }
    
    


    @Then("I should be logged in and see my profile avatar")
    public void i_should_be_logged_in_and_see_my_profile_avatar() {
        // 定位 profileContainer
        WebElement profileContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header"))
        );
    
        // 定位 profileAvatar
        WebElement profileAvatar = profileContainer.findElement(By.cssSelector("groupui-avatar"));
        assertNotNull(profileAvatar, "Profile avatar is not visible, login might have failed.");
    }

    // @When("I wait for the 404 error to disappear")
    // public void i_wait_for_the_404_error_to_disappear() {
    //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    //     try {
    //         // 等待 toast 消息出现并消失
    //         WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
    //             By.cssSelector(".toast-title")
    //         ));
    //         wait.until(ExpectedConditions.invisibilityOf(toastMessage));
    //         System.out.println("Toast message disappeared.");
    //     } catch (TimeoutException e) {
    //         // 如果没有找到 toast 消息，打印日志继续
    //         System.out.println("No blocking toast message found.");
    //     }
    // }
    
    @When("When I see the 404 toast error message visible and wait for the 404 toast error message to be invisible")
    public void i_see_the_404_toast_error_message_visible_and_wait_for_the_404_toast_error_message_to_be_invisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // 固定等待 10 秒，尝试获取 toast 消息
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".toast-title")
        ));
    
        // 等待 toast 消息消失
        wait.until(ExpectedConditions.invisibilityOf(toastMessage));
        System.out.println("Toast message disappeared.");
    }

    @When("I wait 10 seconds before checking the profile avatar visibility")
    public void i_wait_10_seconds_before_checking_the_profile_avatar_visibility() {
        // Fixed wait of 10 seconds
        // try {
        //     Thread.sleep(10000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    
        // Now check whether the avatar is visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // minimal dynamic wait
        WebElement profileContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header"))
        );
    
        WebElement profileAvatar = profileContainer.findElement(By.cssSelector("groupui-avatar"));
    
        if (!profileAvatar.isDisplayed()) {
            throw new RuntimeException("Profile avatar is not visible after 10 seconds plus the extra wait.");
        }
    
        System.out.println("Profile avatar is now visible (after a fixed 10-second wait).");
    }

    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void i_wait_for_toast_error_message_to_become_invisible_and_profile_avatar_to_become_visible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // FIRST wait for any blocking elements, like the toast, to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
    
        // THEN wait for the profile container to be visible
        WebElement profileContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header"))
        );
    
        // Optionally find the avatar to confirm it's present
        WebElement profileAvatar = profileContainer.findElement(By.cssSelector("groupui-avatar"));
        System.out.println("Profile avatar is now visible and not blocked by the toast.");
    }


    @Then("I click the profile avatar")
    public void i_click_the_profile_avatar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // 等待 profileContainer 可见
        WebElement profileContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#app-profile-header")
        ));
    
        // 定位并点击头像
        WebElement profileAvatar = profileContainer.findElement(By.cssSelector("groupui-avatar"));
        if (profileAvatar == null) {
            throw new RuntimeException("Profile avatar not found. Cannot proceed to logout.");
        }
        profileAvatar.click();
    }




@When("I click the logout button")
public void i_click_the_logout_button() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // 使用 CSS 选择器定位 Logout 按钮
    WebElement logoutButton = wait.until(
        ExpectedConditions.elementToBeClickable(By.cssSelector("#app-logout"))
    );

    if (logoutButton == null) {
        throw new RuntimeException("Logout button not found.");
    }

    logoutButton.click();
}

@Then("I should see Username APIKey and Login Button on the login page")
public void i_should_see_Username_APIKey_and_Login_Button_on_the_login_page() {
    // 验证用户名输入框是否可见
    WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("groupui-input[id='welcome-user-name']")
    ));
    assertTrue(usernameInput.isDisplayed(), "Username input field should be visible if login failed.");

    // 验证密码输入框是否可见
    WebElement APIKeyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("groupui-input[id='welcome-api-key']")
    ));
    assertTrue(APIKeyInput.isDisplayed(), "APIKey input field should be visible if login failed.");

    // 验证登录按钮是否可见
    WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("#welcome-login")
    ));
    assertTrue(loginButton.isDisplayed(), "Login button should be visible if login failed.");
}


}
    