// src/test/java/huiyu/DriverManager.java
package huiyu;

import org.openqa.selenium.WebDriver;

public class DriverManager {
    private static WebDriver driver;
    public static void setDriver(WebDriver d) { driver = d; }
    public static WebDriver getDriver()    { return driver; }
    public static void clear()             { driver = null; }
}