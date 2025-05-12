package huiyu.Intermediate_UI_Dependent_Field_Linking_language;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Intermediate_UI_Dependent_Field_Linking_language {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    
    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.get(url);
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 加 wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-user-name']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-input[id='welcome-api-key']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-login")));  
        WebElement usernameField = driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']"));
        WebElement apiKeyField = driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']"));
        WebElement loginButton = driver.findElement(By.cssSelector("#welcome-login"));

        assert usernameField.isDisplayed();
        assert apiKeyField.isDisplayed();
        assert loginButton.isDisplayed();
    }

    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndAPIKey(String username, String apiKey) {
        driver.findElement(By.cssSelector("groupui-input[id='welcome-user-name']")).sendKeys(username);
        driver.findElement(By.cssSelector("groupui-input[id='welcome-api-key']")).sendKeys(apiKey);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        driver.findElement(By.cssSelector("#welcome-login")).click();
    }

    @Then("I should see the terms and conditions popup")
    public void iTermAndConditionsPopupIsDisplayed() {
        WebElement termsAndConditions = driver.findElement(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]"));
        assert termsAndConditions.isDisplayed();
    }

    @When("I move to the accept button to ensure it is visible")
    public void iMoveToTheAcceptButton() {
        WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
        // ((org.openqa.selenium.interactions.Actions) driver).moveToElement(acceptButton);自己生成的错误
        Actions actions = new Actions(driver);
        actions.moveToElement(acceptButton).perform();
    }

    @And("I click the accept button")
    public void iClickTheAcceptButton() {
        driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb")).click();
    }

    // @When("I select {string} in the language dropdown")
    // public void selectLanguage(String lang) {
    //     WebElement selectElem = wait.until(
    //       ExpectedConditions.elementToBeClickable(By.id("changeLanguage"))
    //     );
    //     Select sel = new Select(selectElem);
    //     sel.selectByVisibleText(lang);
    // }

    // @Then("the language dropdown selection should be {string}")
    // public void verifyDropdownSelection(String expected) {
    //     WebElement selectElem = driver.findElement(By.id("changeLanguage"));
    //     Select sel = new Select(selectElem);
    //     String actual = sel.getFirstSelectedOption().getText();
    //     assertEquals(expected, actual, "下拉框选中项不正确");
    // }

    @When("I click the language dropdown to expand it")
    public void 点击下拉框展开() {
        WebElement selectElem = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("changeLanguage"))
        );
        selectElem.click();
        // // 模拟一次点击，展开下拉
        // Actions actions = new Actions(driver);
        // actions.moveToElement(selectElem).click().perform();
    }

    @Then("I should see both English and Deutsch options in the dropdown")
    public void 验证两种选项可见() {
    // 断言 Deutsch 选项可见
        wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#changeLanguage option[value='de']")
        )
        );
        // 断言 English 选项可见
        wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#changeLanguage option[value='en']")
        )
        );
    }

    @When("I select {string} from the dropdown")
    public void 从下拉中选择(String lang) {
        // 点击下拉后，定位并点击具体的 <option>
        By optionLocator = By.xpath(
            "//select[@id='changeLanguage']/option[normalize-space(text())='" + lang + "']"
        );
        WebElement option = wait.until(
            ExpectedConditions.elementToBeClickable(optionLocator)
        );
        option.click();
    }

    @Then("the language dropdown selection should be {string}")
    public void 验证下拉框选中项(String expected) {
        // 断言 <select> 的当前选中项文本
        WebElement selectElem = driver.findElement(By.id("changeLanguage"));
        String actual = new Select(selectElem)
            .getFirstSelectedOption()
            .getText()
            .trim();
        assertEquals(expected, actual, "下拉框选中项不正确");
    }


    @When("I wait for toast error message to become invisible and profile avatar to become visible")
    public void waitForAvatar() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".toast-title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("groupui-avatar")));
    }
    
    @Then("the page title should asynchronously update to {string}")
    public void verifyPageTitleUpdate(String expectedTitle) {
        By titleLocator = By.id("welcome-title");
        // 等待标题文本变成预期
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            titleLocator, expectedTitle
        ));
        WebElement titleElem = driver.findElement(titleLocator);
        assertEquals(expectedTitle, titleElem.getText().trim(),
            "页面标题未按预期更新");
    }


}