// src/test/java/huiyu/stepdefinitions_common/LoginSteps.java
package huiyu.stepdefinitions_common;

import huiyu.BaseTest;
import huiyu.DriverManager;
import huiyu.pages.LoginPage;
import huiyu.pages.TermsPage;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class LoginSteps extends BaseTest {
    private static final String URL      = "http://localhost:4200/";
    private static final String USERNAME = "vw2xn87";
    private static final String APIKEY   =
      "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD";

    @Before
    public void initAndStoreDriver() {
        // 初始化浏览器
        setUp();
        // 将 driver 放入静态持有器
        DriverManager.setDriver(driver);
    }

    @Given("I am logged in")
    public void iAmLoggedIn() {
        // 从 BaseTest.driver 取出、调用 PageObject 完成登录
        LoginPage login = new LoginPage(driver);
        login.open(URL);
        login.login(USERNAME, APIKEY);
        new TermsPage(driver).acceptIfAppears();
    }
}
