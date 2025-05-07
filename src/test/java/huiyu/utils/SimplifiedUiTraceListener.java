// src/test/java/huiyu/utils/SimplifiedUiTraceListener.java
package huiyu.utils; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Import TakesScreenshot
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.File; // Import File
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter; // For timestamp formatting
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.nio.file.Files; // Import Files for directory creation
import java.nio.file.Path;
import java.nio.file.Paths; // Import Paths

public class SimplifiedUiTraceListener implements WebDriverListener, TakesScreenshot {

    private List<Map<String, Object>> traceEvents = new ArrayList<>();
    private String outputFilePath;
    private String screenshotBaseDir; 
    private WebDriver originalDriver;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public SimplifiedUiTraceListener(String outputFilePath, WebDriver originalDriverInstance) {
        this.outputFilePath = outputFilePath;
        if (!(originalDriverInstance instanceof TakesScreenshot)) {
            System.err.println("<<<<< SimplifiedUiTraceListener v6: Original driver does not implement TakesScreenshot. Screenshots will not be available. >>>>>");
        }
        this.originalDriver = originalDriverInstance;

        File traceFile = new File(outputFilePath);
        String runBaseDir = traceFile.getParent();
        this.screenshotBaseDir = runBaseDir + File.separator + "screenshots"; 

        try {
            Files.createDirectories(Paths.get(this.screenshotBaseDir));
             System.out.println("<<<<< SimplifiedUiTraceListener v6: Screenshot directory created: " + this.screenshotBaseDir + " >>>>>");
        } catch (IOException e) {
            System.err.println("<<<<< SimplifiedUiTraceListener v6: Failed to create screenshot directory: " + this.screenshotBaseDir + ". Screenshots will not be saved. Error: " + e.getMessage() + " >>>>>");
            this.screenshotBaseDir = null;
        }
        System.out.println("<<<<< SimplifiedUiTraceListener v6 initialized. Trace Output: " + outputFilePath + " >>>>>");
    }
    
    // Implement getScreenshotAs method from TakesScreenshot interface
    // This allows taking screenshots directly through the listener instance
    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        if (this.originalDriver instanceof TakesScreenshot) {
            return ((TakesScreenshot) this.originalDriver).getScreenshotAs(outputType);
        }
        throw new WebDriverException("Original driver does not implement TakesScreenshot.");
    }

    private String takeScreenshotAndSave(String eventType) {
        if (this.screenshotBaseDir == null || !(this.originalDriver instanceof TakesScreenshot)) {
            return null; // Cannot take or save screenshot
        }
        try {
            File srcFile = this.getScreenshotAs(OutputType.FILE);
            String timestamp = Instant.now().atZone(java.time.ZoneId.systemDefault()).format(TIMESTAMP_FORMATTER);
            String filename = eventType.replaceAll("[^a-zA-Z0-9-_]", "_") + "_" + timestamp + ".png"; // Sanitize eventType
            File destFile = new File(this.screenshotBaseDir, filename);
            org.openqa.selenium.io.FileHandler.copy(srcFile, destFile); // Use Selenium's FileHandler
            System.out.println("<<<<< SimplifiedUiTraceListener v4: Screenshot saved: " + destFile.getAbsolutePath() + " >>>>>");
            Path runBaseDirPath = Paths.get(new File(this.outputFilePath).getParent());
            Path screenshotPath = destFile.toPath();
            String relativePath = runBaseDirPath.relativize(screenshotPath).toString().replace("\\", "/");
            return relativePath;
        } catch (IOException e) {
            System.err.println("<<<<< SimplifiedUiTraceListener v4: Failed to save screenshot for event type " + eventType + ". Error: " + e.getMessage() + " >>>>>");
            return "error_saving_screenshot";
        } catch (WebDriverException e) {
             System.err.println("<<<<< SimplifiedUiTraceListener v4: Failed to take screenshot for event type " + eventType + ". WebDriverException: " + e.getMessage() + " >>>>>");
             return "error_taking_screenshot";
        }
    }


    // getComponentSelector and getPageStateId remain the same

    private String getComponentSelector(WebElement element) { /* ... existing implementation ... */
        if (element == null) return "N/A (element_was_null)";
        StringBuilder selector = new StringBuilder();
        String localTagName = "unknown_tag";
        try {
            localTagName = element.getTagName();
            selector.append(localTagName);
            String id = element.getAttribute("id");
            if (id != null && !id.isEmpty()) selector.append("#").append(id);
            String name = element.getAttribute("name");
            if (name != null && !name.isEmpty()) selector.append("[name='").append(name).append("']");
            String className = element.getAttribute("class");
            if (className != null && !className.trim().isEmpty()) {
                String[] classes = className.trim().split("\\s+");
                if (classes.length > 0 && !classes[0].trim().isEmpty()) selector.append(".").append(classes[0].trim());
            }
            if (Arrays.asList("button", "a", "label", "span", "div").contains(localTagName.toLowerCase())) {
                 String text = element.getText();
                 if (text != null && !text.trim().isEmpty()) {
                     String shortText = text.trim().replaceAll("\\s+", " ");
                     if (shortText.length() > 20) shortText = shortText.substring(0, 17) + "...";
                     selector.append("[text~='").append(shortText.replace("'", "\\'")).append("']");
                 }
            }
            return selector.toString();
        } catch (StaleElementReferenceException e) {
            return (selector.length() > 0 ? selector.toString() : localTagName) + " (stale)";
        } catch (Exception e) {
            return (selector.length() > 0 ? selector.toString() : localTagName) + " (error_in_selector)";
        }
    }
    
     private String getPageStateId() {
         try {
            String dom = this.originalDriver.getPageSource();
            String url = this.originalDriver.getCurrentUrl();
            String combined = url + "::" + dom;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("SimplifiedUiTraceListener v4: Could not get page state ID: " + e.getMessage());
            return "error_page_state_id";
        }
     }


    private void recordInteractionEvent(String eventType, WebElement element, String value) {
        System.out.println("<<<<< SimplifiedUiTraceListener v4: Recording interaction - Type: " + eventType + 
                           ", Element: " + (element != null ? getComponentSelector(element) : "null") + 
                           ", Value: " + (value != null && !value.isEmpty() ? (value.length() > 50 ? value.substring(0,47)+"..." : value) : "N/A") + 
                           " >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("selector", getComponentSelector(element));
        event.put("eventType", eventType);
        if (value != null) {
            event.put("value", value);
        }
        event.put("stateId", getPageStateId());
        
        // --- NEW: Take and save screenshot ---
        String screenshotPath = takeScreenshotAndSave(eventType);
        if (screenshotPath != null) {
            event.put("screenshot", screenshotPath);
        }
        // --- END NEW ---


        this.traceEvents.add(event);
        System.out.println("<<<<< SimplifiedUiTraceListener v4: Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }
    
    private void recordNavigationChangeEvent(String eventType, String url) {
        System.out.println("<<<<< SimplifiedUiTraceListener v4: Recording navigation - Type: " + eventType + ", URL: " + url + " >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("selector", "N/A (navigation)");
        event.put("eventType", eventType);
        if (url != null) {
            event.put("url", url);
        }
        event.put("stateId", getPageStateId());

        // --- NEW: Take and save screenshot ---
         String screenshotPath = takeScreenshotAndSave(eventType);
        if (screenshotPath != null) {
            event.put("screenshot", screenshotPath);
        }
        // --- END NEW ---

        this.traceEvents.add(event);
        System.out.println("<<<<< SimplifiedUiTraceListener v4: Nav Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }

    // --- WebDriverListener Event Implementations (Relying on AnyCall) ---

    @Override
    public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
         String methodName = method.getName();
         // System.out.println(String.format("<<<<< SimplifiedUiTraceListener v4: afterAnyWebDriverCall -> Method: %s >>>>>", methodName)); // Too verbose

         if ("get".equals(methodName) && args != null && args.length > 0 && args[0] instanceof String) {
             System.out.println("<<<<< SimplifiedUiTraceListener v4: Detected 'get' in afterAnyWebDriverCall. URL: " + args[0] + " >>>>>");
             recordNavigationChangeEvent("get_url", (String) args[0]);
         } else if ("perform".equals(methodName) && target instanceof Actions) { // Actions.perform()
             System.out.println("<<<<< SimplifiedUiTraceListener v4: Detected 'perform' in afterAnyWebDriverCall (Actions). >>>>>");
             // Actions perform multiple steps. Hard to get *which* element was acted on last.
             // We could potentially log the type of action sequence if args provides enough info.
             // For now, just log that Actions were performed.
             // recordNavigationChangeEvent("actions_perform", "N/A"); // Or create a specific event type
         }
         // Note: findElement calls are handled by afterAnyWebElementCall on the result element
         // Navigation.to(), Back, Forward, Refresh are handled by their specific afterXxx methods
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        String methodName = method.getName();
        // String elementDesc = getComponentSelector(element); // Logged inside recordInteractionEvent
        // System.out.println(String.format("<<<<< SimplifiedUiTraceListener v4: afterAnyWebElementCall -> Element: %s, Method: %s >>>>>", elementDesc, methodName)); // Too verbose

        if ("click".equals(methodName)) {
            recordInteractionEvent("click", element, null);
        } else if ("sendKeys".equals(methodName) && args != null && args.length > 0 && args[0] instanceof CharSequence[]) {
            CharSequence[] keysToSendArray = (CharSequence[]) args[0];
            StringBuilder sb = new StringBuilder();
            if (keysToSendArray != null) {
                for (CharSequence cs : keysToSendArray) {
                    if (cs != null) {
                        sb.append(cs);
                    }
                }
            }
            recordInteractionEvent("sendKeys", element, sb.toString());
        } else if ("clear".equals(methodName)) {
            recordInteractionEvent("clear", element, null);
        } else if ("submit".equals(methodName)) {
             recordInteractionEvent("submit", element, null);
        }
        // Note: getText, isDisplayed etc. calls are not recorded as interactions by default
    }
    
    // Specific listeners - keeping these for interface compliance, but recording handled by AnyCall
    @Override public void afterTo(WebDriver.Navigation navigation, String url) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterTo triggered. URL: " + url + " >>>>>"); recordNavigationChangeEvent("navigateTo_to", url); }
    @Override public void afterBack(WebDriver.Navigation navigation) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterBack triggered. >>>>>"); recordNavigationChangeEvent("navigateBack", this.originalDriver.getCurrentUrl()); }
    @Override public void afterForward(WebDriver.Navigation navigation) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterForward triggered. >>>>>"); recordNavigationChangeEvent("navigateForward", this.originalDriver.getCurrentUrl()); }
    @Override public void afterRefresh(WebDriver.Navigation navigation) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterRefresh triggered. >>>>>"); recordNavigationChangeEvent("navigateRefresh", this.originalDriver.getCurrentUrl()); }
    @Override public void afterClick(WebElement element, WebDriver driver) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterClick triggered. Element: " + getComponentSelector(element) + " >>>>>"); } // Recording handled by afterAnyWebElementCall
    @Override public void afterSendKeys(WebElement element, WebDriver driver, CharSequence... keysToSend) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterSendKeys triggered. Element: " + getComponentSelector(element) + " >>>>>"); } // Recording handled by afterAnyWebElementCall
    @Override public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) { System.out.println("<<<<< SimplifiedUiTraceListener v4: Specific afterExecuteScript triggered. >>>>>"); Map<String, Object> event = new HashMap<>(); event.put("timestamp", Instant.now().toString()); event.put("selector", "N/A (executeScript)"); event.put("eventType", "executeScript"); event.put("value", script.substring(0, Math.min(script.length(), 200)) + (script.length() > 200 ? "..." : "")); event.put("stateId", getPageStateId()); this.traceEvents.add(event); System.out.println("<<<<< SimplifiedUiTraceListener v4: Script Event added (specific). Total events: " + this.traceEvents.size() + " >>>>>"); }


    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        System.err.println("<<<<< SimplifiedUiTraceListener v4: onError. Method: " + method.getName() +
                           ", Ex: " + targetException.getClass().getSimpleName() +
                           ", Msg: " + targetException.getMessage().split("\n")[0] + " >>>>>");

        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("eventType", "exceptionListener");
        event.put("methodName", method.getName());
        if (args != null && args.length > 0 && args[0] instanceof By) {
            event.put("locator", args[0].toString());
        } else if (args != null && args.length > 1 && args[1] instanceof By) {
             event.put("locator", args[1].toString());
        }
        event.put("exceptionType", targetException.getClass().getName());
        String errorMessage = targetException.getMessage();
        if (errorMessage != null && errorMessage.length() > 300) { 
            errorMessage = errorMessage.substring(0, 297) + "...";
        }
        event.put("errorMessage", errorMessage);
        event.put("stateId", getPageStateId());

        // Optionally take screenshot on error
         String screenshotPath = takeScreenshotAndSave("error_" + method.getName());
        if (screenshotPath != null) {
            event.put("screenshot", screenshotPath);
        }
        
        this.traceEvents.add(event);
        System.err.println("<<<<< SimplifiedUiTraceListener v4: Exception Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }

    public void saveTrace() {
        System.out.println("<<<<< SimplifiedUiTraceListener v4: Saving trace. Events: " + this.traceEvents.size() + " >>>>>");
        if (this.outputFilePath == null || this.outputFilePath.trim().isEmpty()) {
            System.err.println("SimplifiedUiTraceListener v4: Output file path is not set. Trace not saved.");
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(this.outputFilePath)) {
            gson.toJson(this.traceEvents, writer);
            System.out.println("<<<<< SimplifiedUiTraceListener v4: Trace saved to " + this.outputFilePath + " (" + this.traceEvents.size() + " events) >>>>>");
        } catch (IOException e) {
            System.err.println("<<<<< SimplifiedUiTraceListener v4: Failed to save trace to " + this.outputFilePath + " >>>>>");
            e.printStackTrace();
        }
    }

    @Override public void beforeAnyCall(Object target, Method method, Object[] args) { /* Too verbose for regular run */ }
    @Override public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) { /* Too verbose */ }
    @Override public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) { /* Too verbose */ }
}