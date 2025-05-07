package huiyu.stepdefinitions_login_testUI;

import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringDecorator; // For Selenium 4+
import org.openqa.selenium.support.ui.ExpectedConditions; // For Selenium 4+
import org.openqa.selenium.support.ui.WebDriverWait; // 需要导入这个

import huiyu.utils.UiTraceListener; // 如果需要更底层的操作
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
// public class Login extends BaseTest {
public class Login {
    // private WebDriverWait wait;

    // @Before
    // public void init() {
    //     setUp();
    //     wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    // }
    private WebDriver originalDriver; // Store the raw driver
    private WebDriver driver;         // This will be the decorated driver
    private WebDriverWait wait;
    private UiTraceListener traceListener;

    @Before
    public void setUp() {
        // 根据你的实际情况选择 WebDriverManager 或直接设置 driver 路径
        WebDriverManager.firefoxdriver().setup();
        originalDriver = new FirefoxDriver(); // 1. 创建原始的 WebDriver

        String traceFileName = "trace_login_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId() + ".ui.trace.json";
        // 确保路径在 target 目录下，这样 mvn clean 会清理掉
        String traceFilePath = Paths.get("target", "ui-traces", traceFileName).toString(); 
        // 创建父目录，如果不存在
        try {
            Paths.get("target", "ui-traces").toFile().mkdirs();
        } catch (Exception e) {
            System.err.println("Could not create directory for traces: " + e.getMessage());
        }


        // 2. 创建 Listener 实例，并传入 *原始的* driver
        traceListener = new UiTraceListener(traceFilePath, originalDriver);

        // 3. 正确使用 EventFiringDecorator
        //    构造函数接收 WebDriverListener(s)
        //    decorate 方法接收 WebDriver 实例
        EventFiringDecorator<WebDriver> decorator = new EventFiringDecorator<>(traceListener);
        driver = decorator.decorate(originalDriver); // decorate 方法接收原始 driver

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    private WebElement findElementInShadowDom(By hostSelector, String innerSelector) {
        // 1. 找到 Shadow Host 元素
        WebElement shadowHost = wait.until(ExpectedConditions.presenceOfElementLocated(hostSelector));

        // 2. 获取 Shadow Root
        // Selenium 4+ 的标准方法
        SearchContext shadowRoot = shadowHost.getShadowRoot();

        // 3. 在 Shadow Root 内部查找元素
        return shadowRoot.findElement(By.cssSelector(innerSelector));
    }
    private WebElement findElementViaLightDomToShadowDom(
                                                    By outerComponentSelector, 
                                                    String intermediateHostSelectorInLightDom, 
                                                    String targetSelectorInInnerShadow) {
        
        System.out.println("--- Debug: Entering findElementViaLightDomToShadowDom ---");
        System.out.println("Debug: outerComponentSelector = " + outerComponentSelector.toString());
        System.out.println("Debug: intermediateHostSelectorInLightDom = " + intermediateHostSelectorInLightDom);
        System.out.println("Debug: targetSelectorInInnerShadow = " + targetSelectorInInnerShadow);

        // 1. 找到最外层的组件 (e.g., groupui-card)
        WebElement outerComponent = null;
        try {
            System.out.println("Debug: Attempting to find outerComponent...");
            outerComponent = wait.until(ExpectedConditions.presenceOfElementLocated(outerComponentSelector));
            System.out.println("Debug: Found outerComponent: " + (outerComponent != null ? outerComponent.getTagName() : "null"));
        } catch (Exception e) {
            System.err.println("Debug: FAILED to find outerComponent with selector: " + outerComponentSelector.toString());
            e.printStackTrace(); // 打印完整的异常堆栈
            throw e; // 重新抛出异常，让测试失败
        }

        // 2. 在 outerComponent 的常规子节点 (Light DOM) 中找到中间的 Shadow Host (e.g., groupui-input)
        WebElement intermediateShadowHost = null;
        try {
            System.out.println("Debug: Attempting to find intermediateShadowHost in Light DOM of outerComponent using selector: " + intermediateHostSelectorInLightDom);
            intermediateShadowHost = outerComponent.findElement(By.cssSelector(intermediateHostSelectorInLightDom));
            System.out.println("Debug: Found intermediateShadowHost: " + (intermediateShadowHost != null ? intermediateShadowHost.getTagName() + " with id " + intermediateShadowHost.getAttribute("id") : "null"));
        } catch (Exception e) {
            System.err.println("Debug: FAILED to find intermediateShadowHost in Light DOM of outerComponent with selector: " + intermediateHostSelectorInLightDom);
            System.err.println("Debug: Outer component was: " + (outerComponent != null ? outerComponent.getTagName() : "null"));
            e.printStackTrace();
            throw e;
        }
        
        // 3. 进入中间 Shadow Host 的 Shadow Root
        SearchContext innerShadowRoot = null;
        try {
            System.out.println("Debug: Attempting to get ShadowRoot from intermediateShadowHost (" + intermediateShadowHost.getTagName() + ")");
            innerShadowRoot = intermediateShadowHost.getShadowRoot();
            System.out.println("Debug: Got innerShadowRoot: " + (innerShadowRoot != null));
        } catch (Exception e) {
            System.err.println("Debug: FAILED to get ShadowRoot from intermediateShadowHost: " + intermediateShadowHost.getTagName());
            e.printStackTrace();
            throw e;
        }
        
        // 4. 在这个 Shadow Root 中找到最终目标元素
        WebElement targetElement = null;
        try {
            System.out.println("Debug: Attempting to find targetElement in innerShadowRoot using selector: " + targetSelectorInInnerShadow);
            targetElement = innerShadowRoot.findElement(By.cssSelector(targetSelectorInInnerShadow));
            System.out.println("Debug: Found targetElement: " + (targetElement != null ? targetElement.getTagName() : "null"));
        } catch (Exception e) {
            System.err.println("Debug: FAILED to find targetElement in innerShadowRoot with selector: " + targetSelectorInInnerShadow);
            System.err.println("Debug: Inner Shadow Root was: " + (innerShadowRoot != null));
            e.printStackTrace();
            throw e;
        }
        
        System.out.println("--- Debug: Exiting findElementViaLightDomToShadowDom successfully ---");
        return targetElement;
    }
    private WebElement findElementInDoubleShadowDom(By outerHostSelector, 
                                                    String firstInnerHostSelectorInOuterShadow, 
                                                    String targetSelectorInInnerShadow) {
        // 1. 找到最外层的 Shadow Host (groupui-card)
        WebElement outerShadowHost = wait.until(ExpectedConditions.presenceOfElementLocated(outerHostSelector));
        
        // 2. 进入第一层 Shadow Root
        SearchContext firstShadowRoot = outerShadowHost.getShadowRoot();
        
        // 3. 在第一层 Shadow Root 中找到内层的 Shadow Host (e.g., groupui-input)
        WebElement innerShadowHost = firstShadowRoot.findElement(By.cssSelector(firstInnerHostSelectorInOuterShadow));
        
        // 4. 进入第二层 Shadow Root
        SearchContext secondShadowRoot = innerShadowHost.getShadowRoot();
        
        // 5. 在第二层 Shadow Root 中找到最终目标元素
        return secondShadowRoot.findElement(By.cssSelector(targetSelectorInInnerShadow));
    }

    private WebElement waitForElementInShadowDomVisible(By hostSelector, String innerSelector) {
        WebElement shadowHost = wait.until(ExpectedConditions.presenceOfElementLocated(hostSelector));
        SearchContext shadowRoot = shadowHost.getShadowRoot();

        // 等待 Shadow DOM 内部的元素可见
        // 注意：ExpectedConditions 可能不直接支持在 SearchContext (shadowRoot) 上下文的等待
        // 我们可能需要一个自定义的 ExpectedCondition 或者使用更底层的 JavaScript
        // 简单的轮询方式 (不推荐用于生产，但用于演示)：
        // for (int i = 0; i < 10; i++) {
        //     try {
        //         WebElement element = shadowRoot.findElement(By.cssSelector(innerSelector));
        //         if (element.isDisplayed()) return element;
        //     } catch (Exception e) {
        //         // 忽略并重试
        //     }
        //     try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        // }
        // throw new TimeoutException("Element " + innerSelector + " not visible in shadow DOM of " + hostSelector);

        // 更健壮的方式是使用 JavascriptExecutor 来检查可见性，或者期望它存在然后进行操作
        return wait.until(driver -> {
            try {
                WebElement element = shadowHost.getShadowRoot().findElement(By.cssSelector(innerSelector));
                return element.isDisplayed() ? element : null;
            } catch (Exception e) {
                return null;
            }
        });
    }


    @Given("I open the login page {string}")
    public void iOpenTheLoginPage(String url) {
        driver.get(url);
        if (traceListener != null) { // 手动记录导航
            traceListener.manuallyRecordNavigation("navigateTo_to_manual", url);
        }
    }

    @Then("I should see Username APIKey and Login Button on the login page")
    public void iShouldSeeUsernameAPIKeyAndLoginButtonOnTheLoginPage() {
        By outerCardSelector = By.cssSelector("app-welcome > groupui-card"); 
        
        WebElement usernameField = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-input#welcome-user-name", "input[part='input']");
        assertNotNull(usernameField, "Username input not found");
        // 手动记录“找到元素”或“断言元素存在”的事件，如果需要的话
        // if (traceListener != null) {
        //     traceListener.manuallyRecordInteraction("assertVisible", usernameField, "Username Input");
        // }

        WebElement apiKeyField = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-input#welcome-api-key", "input[part='input']");
        assertNotNull(apiKeyField, "APIKey input not found");

        WebElement loginButtonElement = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-button#welcome-login", "div#button");
        assertNotNull(loginButtonElement, "Login button not found");
    }

    @When("I enter username {string} and APIKey {string}")
    public void iEnterUsernameAndAPIKey(String username, String apiKey) {
        By outerCardSelector = By.cssSelector("app-welcome > groupui-card");

        WebElement usernameInputElement = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-input#welcome-user-name", "input[part='input']");
        usernameInputElement.sendKeys(username);
        if (traceListener != null) { // 手动记录 sendKeys
            traceListener.manuallyRecordInteraction("sendKeys_manual", usernameInputElement, username);
        }
 
        WebElement apiKeyInputElement = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-input#welcome-api-key", "input[part='input']");
        apiKeyInputElement.sendKeys(apiKey);
        if (traceListener != null) { // 手动记录 sendKeys
            traceListener.manuallyRecordInteraction("sendKeys_manual", apiKeyInputElement, "****"); // 不记录真实密码
        }
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        By outerCardSelector = By.cssSelector("app-welcome > groupui-card");
        WebElement loginButtonInternalElement = findElementViaLightDomToShadowDom(
                outerCardSelector, "groupui-button#welcome-login", "div#button");
        loginButtonInternalElement.click();
        if (traceListener != null) { // 手动记录 click
            traceListener.manuallyRecordInteraction("click_manual", loginButtonInternalElement, null);
        }
    }
        @Then("I should see the terms and conditions popup")
        public void iShouldSeeTheTermsAndConditionsPopup() {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")));
        }
    
        @When("I move to the accept button to ensure it is visible")
        public void iMoveToTheAcceptButtonToEnsureItIsVisible() {
            WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
            Actions actions = new Actions(driver);
            actions.moveToElement(acceptButton).perform();
        }
    
        @And("I click the accept button")
        public void iClickTheAcceptButton() {
            WebElement acceptButton = driver.findElement(By.cssSelector("groupui-button#welcome-agree-agb"));
            acceptButton.click();
        }
    
        @Then("I should be logged in and see my profile avatar")
        public void iShouldBeLoggedInAndSeeMyProfileAvatar() {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app-profile-header")));
            WebElement profileAvatar = driver.findElement(By.cssSelector("groupui-avatar"));
            assertNotNull(profileAvatar);
        }
        @After
        public void tearDown() {
            if (traceListener != null) {
                traceListener.saveTrace(); 
            }
            if (driver != null) { // 应该关闭包装后的 driver，它会负责关闭原始 driver
                driver.quit();
            }
            // originalDriver 不需要单独 quit，因为包装后的 driver.quit() 会处理它
        }
}
