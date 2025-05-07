// src/test/java/huiyu/utils/SimplifiedUiTraceListener.java
package huiyu.utils; // 确保包名正确

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SimplifiedUiTraceListener implements WebDriverListener {

    private List<Map<String, Object>> traceEvents = new ArrayList<>();
    private String outputFilePath;
    private WebDriver originalDriver;

    public SimplifiedUiTraceListener(String outputFilePath, WebDriver originalDriverInstance) {
        this.outputFilePath = outputFilePath;
        this.originalDriver = originalDriverInstance;
        System.out.println("<<<<< SimplifiedUiTraceListener v3 initialized. Output: " + outputFilePath + " >>>>>");
    }

    private String getComponentSelector(WebElement element) {
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
            System.err.println("SimplifiedUiTraceListener v3: Could not get page state ID: " + e.getMessage());
            return "error_page_state_id";
        }
    }

    private void recordInteractionEvent(String eventType, WebElement element, String value) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Recording interaction - Type: " + eventType + 
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
        this.traceEvents.add(event);
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }
    
    private void recordNavigationChangeEvent(String eventType, String url) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Recording navigation - Type: " + eventType + ", URL: " + url + " >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("selector", "N/A (navigation)");
        event.put("eventType", eventType);
        if (url != null) {
            event.put("url", url);
        }
        event.put("stateId", getPageStateId());
        this.traceEvents.add(event);
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Nav Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }

    // --- WebDriverListener Event Implementations ---

    // We will rely on afterAnyWebDriverCall and afterAnyWebElementCall primarily
    // Specific listeners like afterClick, afterSendKeys etc., are kept for interface compliance 
    // but their main logic will be triggered via the "AnyCall" listeners.

    @Override
    public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
         String methodName = method.getName();
         System.out.println(String.format("<<<<< SimplifiedUiTraceListener v3: afterAnyWebDriverCall -> Method: %s >>>>>", methodName));
         
         if ("get".equals(methodName) && args != null && args.length > 0 && args[0] instanceof String) {
             System.out.println("<<<<< SimplifiedUiTraceListener v3: Detected 'get' in afterAnyWebDriverCall. URL: " + args[0] + " >>>>>");
             recordNavigationChangeEvent("get_url", (String) args[0]);
         }
         // Other WebDriver direct methods like executeScript could be handled here if they don't have specific listeners
         // or if specific listeners aren't firing.
         // For executeScript, we have a specific afterExecuteScript, so we'll rely on that.
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        String methodName = method.getName();
        String elementDesc = getComponentSelector(element);
        System.out.println(String.format("<<<<< SimplifiedUiTraceListener v3: afterAnyWebElementCall -> Element: %s, Method: %s >>>>>", elementDesc, methodName));

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
        }
        // Other WebElement methods like submit(), etc., could be handled here.
    }
    
    // Specific listeners - these might not fire if afterAnyXCall already handles them,
    // or they might fire in addition. We need to see the logs.
    // For now, let's keep their logging to see if they are invoked.
    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterTo triggered. URL: " + url + " >>>>>");
        // Potentially redundant if afterAnyWebDriverCall handles driver.get() which becomes navigate().to()
        // To avoid double logging, we might prefer handling in afterAnyWebDriverCall or ensure this is distinct
        // Let's assume driver.get() triggers this path more reliably for "to" operations
        recordNavigationChangeEvent("navigateTo_to", url);
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterBack triggered. >>>>>");
        recordNavigationChangeEvent("navigateBack", this.originalDriver.getCurrentUrl());
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
         System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterForward triggered. >>>>>");
        recordNavigationChangeEvent("navigateForward", this.originalDriver.getCurrentUrl());
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterRefresh triggered. >>>>>");
        recordNavigationChangeEvent("navigateRefresh", this.originalDriver.getCurrentUrl());
    }
    
    // These specific interaction listeners might be redundant if afterAnyWebElementCall works.
    // Keep them for now to see if they fire. If they do, and afterAnyWebElementCall also fires for the same action,
    // we'll need to decide where to put the recordInteractionEvent call to avoid duplicates.
    @Override
    public void afterClick(WebElement element, WebDriver driver) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterClick triggered. Element: " + getComponentSelector(element) + " >>>>>");
        // If afterAnyWebElementCall handles 'click', this might lead to double recording.
        // For now, let's comment out the recording here and rely on afterAnyWebElementCall.
        // recordInteractionEvent("click_specific", element, null); 
    }

    @Override
    public void afterSendKeys(WebElement element, WebDriver driver, CharSequence... keysToSend) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterSendKeys triggered. Element: " + getComponentSelector(element) + " >>>>>");
        // If afterAnyWebElementCall handles 'sendKeys', this might lead to double recording.
        // For now, let's comment out the recording here.
        // StringBuilder sb = new StringBuilder();
        // if (keysToSend != null) { 
        //     for (CharSequence cs : keysToSend) {
        //         if (cs != null) { 
        //             sb.append(cs);
        //         }
        //     }
        // }
        // recordInteractionEvent("sendKeys_specific", element, sb.toString());
    }
    
    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Specific afterExecuteScript triggered. >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("selector", "N/A (executeScript)");
        event.put("eventType", "executeScript");
        event.put("value", script.substring(0, Math.min(script.length(), 200)) + (script.length() > 200 ? "..." : ""));
        event.put("stateId", getPageStateId());
        this.traceEvents.add(event);
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Script Event added (specific). Total events: " + this.traceEvents.size() + " >>>>>");
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        System.err.println("<<<<< SimplifiedUiTraceListener v3: onError. Method: " + method.getName() +
                           ", Ex: " + targetException.getClass().getSimpleName() +
                           ", Msg: " + targetException.getMessage().split("\n")[0] + " >>>>>"); // Shorter msg

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
        
        this.traceEvents.add(event);
        System.err.println("<<<<< SimplifiedUiTraceListener v3: Exception Event added. Total events: " + this.traceEvents.size() + " >>>>>");
    }

    public void saveTrace() {
        System.out.println("<<<<< SimplifiedUiTraceListener v3: Saving trace. Events: " + this.traceEvents.size() + " >>>>>");
        if (this.outputFilePath == null || this.outputFilePath.trim().isEmpty()) {
            System.err.println("SimplifiedUiTraceListener v3: Output file path is not set. Trace not saved.");
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(this.outputFilePath)) {
            gson.toJson(this.traceEvents, writer);
            System.out.println("<<<<< SimplifiedUiTraceListener v3: Trace saved to " + this.outputFilePath + " (" + this.traceEvents.size() + " events) >>>>>");
        } catch (IOException e) {
            System.err.println("<<<<< SimplifiedUiTraceListener v3: Failed to save trace to " + this.outputFilePath + " >>>>>");
            e.printStackTrace();
        }
    }

    // beforeXxx methods are not strictly necessary for this logging but kept for completeness if debug needed
    @Override public void beforeAnyCall(Object target, Method method, Object[] args) {
        System.out.println(String.format("<<<<< SimplifiedUiTraceListener v3: beforeAnyCall -> Target: %s, Method: %s >>>>>", target.getClass().getSimpleName(), method.getName()));
    }
    @Override public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
        System.out.println(String.format("<<<<< SimplifiedUiTraceListener v3: beforeAnyWebDriverCall -> Method: %s >>>>>", method.getName()));
    }
    @Override public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
        System.out.println(String.format("<<<<< SimplifiedUiTraceListener v3: beforeAnyWebElementCall -> Element: %s, Method: %s >>>>>", getComponentSelector(element), method.getName()));
    }
    // ... other before methods can be added if needed for deeper debugging
}