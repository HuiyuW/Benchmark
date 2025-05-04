package huiyu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
// import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {
    protected WebDriver driver;
    protected static final List<Double> accessibilityScores = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        
        // System.setProperty("webdriver.chrome.driver", Config.DRIVER_PATH);
        // WebDriverManager.chromedriver().setup();
        
        // ChromeOptions options = new ChromeOptions();
        // options.addArguments("--remote-allow-origins=*");
        // // options.addArguments("--remote-debugging-port=" + Config.LIGHTHOUSE_PORT); 
        // driver = new ChromeDriver(options);


        WebDriverManager.firefoxdriver().setup();

        // 如果需要无头模式（Headless），可以添加参数
        FirefoxOptions options = new FirefoxOptions();
        // options.addArguments("--headless"); // 如需无界面模式，可打开此行注释
        // options.addArguments(
        //     "--remote-allow-origins=*"
        // );
        driver = new FirefoxDriver(options);
        System.out.println("FirefoxDriver initialized and Firefox launched."); 
        // System.out.println("ChromeDriver initialized and Chrome launched."); 
        // System.out.println("Edge initialized and Edge launched."); 
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            System.out.println("Closing WebDriver...");
            driver.quit();
            driver = null;
            System.out.println("WebDriver closed successfully.");
        }
    }

    @AfterAll
    public static void runLighthouse() {
        try {
            
            LighthouseUtil2.lighthouseReport(Config.TEST_URL, Config.LIGHTHOUSE_REPORT_NAME, Config.LIGHTHOUSE_PORT);
            accessibilityScores.add(LighthouseUtil2.getAccessibilityScore(Config.LIGHTHOUSE_REPORT_NAME));

            
            double averageScore = accessibilityScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            System.out.println("Average Accessibility Score: " + averageScore);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

