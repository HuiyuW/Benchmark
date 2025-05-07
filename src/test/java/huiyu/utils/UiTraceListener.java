// src/test/java/huiyu/utils/UiTraceListener.java
package huiyu.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays; // For Arrays.toString

public class UiTraceListener implements WebDriverListener {

    private List<Map<String, Object>> traceEvents = new ArrayList<>();
    private String outputFilePath;
    private WebDriver originalDriver;

    // A flag to help identify if an exception is likely from a WebDriverWait polling attempt
    private static final ThreadLocal<Boolean> isWebDriverWaitPolling = ThreadLocal.withInitial(() -> false);

    public UiTraceListener(String outputFilePath, WebDriver originalDriverInstance) {
        this.outputFilePath = outputFilePath;
        this.originalDriver = originalDriverInstance;
        System.out.println("<<<<< UiTraceListener initialized. Output path: " + outputFilePath + " >>>>>");
    }

    // --- Overriding findElement methods to set a flag for WebDriverWait polling ---
    // We can't directly know if findElement is called by WebDriverWait,
    // but we can infer. A more robust solution would require deeper integration or AOP.
    // This is a simplification.
    @Override
    public void beforeFindElement(WebDriver driver, By by) {
        // A heuristic: if findElement is called répétitively, it might be WebDriverWait
        // For now, we'll rely on the onError logic to filter some NoSuchElementExceptions.
        // System.out.println("<<<<< DEBUG UiTraceListener: beforeFindElement " + by.toString() + " >>>>>");
    }

    @Override
    public void afterFindElement(WebDriver driver, By by, WebElement result) {
        // System.out.println("<<<<< DEBUG UiTraceListener: afterFindElement " + by.toString() + (result == null ? " NOT FOUND" : " FOUND") + " >>>>>");
        // Optionally log findElement success if needed, but can be very verbose
        // recordEvent("findElementSuccess", result, by.toString());
    }


    private String getElementSelector(WebElement element) {
        if (element == null) return "N/A (element was null)";
        StringBuilder selector = new StringBuilder();
        try {
            String tagName = element.getTagName();
            selector.append(tagName);

            String id = element.getAttribute("id");
            if (id != null && !id.isEmpty()) {
                selector.append("#").append(id);
            }

            String name = element.getAttribute("name");
            if (name != null && !name.isEmpty()) {
                selector.append("[name='").append(name).append("']");
            }
            
            // Try to get 'part' attribute, common in Web Components
            String part = element.getAttribute("part");
            if (part != null && !part.isEmpty()) {
                selector.append("[part='").append(part).append("']");
            }

            String className = element.getAttribute("class");
            if (className != null && !className.trim().isEmpty()) {
                String[] classes = className.trim().split("\\s+");
                for (int i = 0; i < Math.min(classes.length, 2); i++) { // Max 2 classes
                    if(!classes[i].trim().isEmpty()) {
                        selector.append(".").append(classes[i].trim());
                    }
                }
            }
            
            // Try to get text content for some elements (like buttons) for better description
            // Be careful, as this can be long or sensitive.
            if (Arrays.asList("button", "a", "span", "div", "label").contains(tagName.toLowerCase())) {
                 String text = element.getText();
                 if (text != null && !text.trim().isEmpty()) {
                     String shortText = text.trim();
                     if (shortText.length() > 30) {
                         shortText = shortText.substring(0, 27) + "...";
                     }
                     selector.append("[text~='").append(shortText.replace("'", "\\'")).append("']");
                 }
            }
            return selector.toString();
        } catch (StaleElementReferenceException e) {
            return selector.length() > 0 ? selector.append(" (stale)").toString() : "N/A (stale element)";
        } catch (Exception e) {
            System.err.println("Error getting element selector: " + e.getMessage() + " for element: " + (selector.length() > 0 ? selector.toString() : "unknown"));
            return selector.length() > 0 ? selector.toString() : "N/A (error getting selector)";
        }
    }

    private String getPageStateId(WebDriver driverInstanceToUse) {
        try {
            String dom = driverInstanceToUse.getPageSource(); // Use passed driver, should be originalDriver
            String url = driverInstanceToUse.getCurrentUrl();
            String combined = url + "::" + dom; // Added separator for clarity
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not found for page state ID.");
            e.printStackTrace();
            return "error_hashing_state";
        } catch (Exception e) {
            System.err.println("Could not get page state ID: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            // Don't print full stack trace here to avoid flooding logs, but log the error type.
            return "error_getting_state";
        }
    }

    private void recordEvent(String eventType, WebElement element, String value) {
        System.out.println("<<<<< DEBUG UiTraceListener: recordEvent called - EventType: " + eventType + 
                           ", Element: " + (element != null ? element.getTagName() : "null") + 
                           ", Value: " + (value != null ? value : "N/A") + " >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        
        String selectorStr = "N/A (selector not resolved)";
        if (element != null) {
            try {
                selectorStr = getElementSelector(element);
            } catch (Exception e) {
                System.err.println("Exception during getElementSelector in recordEvent: " + e.getMessage());
            }
        }
        event.put("selector", selectorStr);
        event.put("eventType", eventType);
        if (value != null) {
            event.put("value", value);
        }
        
        String stateIdStr = "N/A (stateId not resolved)";
        try {
            stateIdStr = getPageStateId(this.originalDriver);
        } catch (Exception e) {
            System.err.println("Exception during getPageStateId in recordEvent: " + e.getMessage());
        }
        event.put("stateId", stateIdStr);
        
        this.traceEvents.add(event);
        System.out.println("<<<<< DEBUG UiTraceListener: Event added to traceEvents. Current list size: " + this.traceEvents.size() + " >>>>>");
        // System.out.println("<<<<< DEBUG UiTraceListener: Recorded Event Details: " + new Gson().toJson(event) + " >>>>>"); // Can be verbose
    }
    
    private void recordNavigationEvent(String eventType, String url) {
        System.out.println("<<<<< DEBUG UiTraceListener: recordNavigationEvent called - EventType: " + eventType + ", URL: " + url + " >>>>>");
        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("selector", "N/A (navigation)");
        event.put("eventType", eventType);
        event.put("url", url);
        
        String stateIdStr = "N/A (stateId not resolved)";
        try {
            stateIdStr = getPageStateId(this.originalDriver);
        } catch (Exception e) {
            System.err.println("Exception during getPageStateId in recordNavigationEvent: " + e.getMessage());
        }
        event.put("stateId", stateIdStr);

        this.traceEvents.add(event);
        System.out.println("<<<<< DEBUG UiTraceListener: Navigation Event added to traceEvents. Current list size: " + this.traceEvents.size() + " >>>>>");
    }
    // 在 UiTraceListener.java 中添加
    public void manuallyRecordInteraction(String eventType, WebElement element, String value) {
        System.out.println("<<<<< UiTraceListener: MANUALLY recording event - Type: " + eventType + 
                        ", Element: " + (element != null ? element.getTagName() : "null") + 
                        ", Value: " + (value != null ? value : "N/A") + " >>>>>");
        // 调用现有的 recordEvent 逻辑
        // 注意：这里的 element 是交互的最终目标元素
        recordEvent(eventType, element, value); 
    }

    public void manuallyRecordNavigation(String eventType, String url) {
        System.out.println("<<<<< UiTraceListener: MANUALLY recording navigation - Type: " + eventType + ", URL: " + url + " >>>>>");
        recordNavigationEvent(eventType, url);
    }
    // --- WebDriverListener 方法覆盖 ---

    // @Override
    // public void afterClick(WebElement element, WebDriver driver) {
    //     System.out.println("<<<<< DEBUG UiTraceListener: afterClick triggered for element: " + (element != null ? element.getTagName() : "null element") + " >>>>>");
    //     recordEvent("click", element, null);
    // }

    // @Override
    // public void afterSendKeys(WebElement element, WebDriver driver, CharSequence... keysToSend) {
    //      System.out.println("<<<<< DEBUG UiTraceListener: afterSendKeys triggered for element: " + (element != null ? element.getTagName() : "null element") + " >>>>>");
    //     StringBuilder sb = new StringBuilder();
    //     for (CharSequence cs : keysToSend) {
    //         sb.append(cs);
    //     }
    //     recordEvent("sendKeys", element, sb.toString());
    // }
    
    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        System.out.println("<<<<< DEBUG UiTraceListener: afterExecuteScript triggered >>>>>");
        recordEvent("executeScript", null, script.substring(0, Math.min(script.length(), 100)) + "...");
    }
    
    // @Override
    // public void afterTo(WebDriver.Navigation navigation, String url, WebDriver driver) {
    //     System.out.println("<<<<< DEBUG UiTraceListener: afterTo triggered with URL: " + url + " >>>>>");
    //     recordNavigationEvent("navigateTo_to", url);
    // }

    // @Override
    // public void afterBack(WebDriver.Navigation navigation, WebDriver driver) {
    //     System.out.println("<<<<< DEBUG UiTraceListener: afterBack triggered >>>>>");
    //     recordNavigationEvent("navigateBack", this.originalDriver.getCurrentUrl());
    // }

    // @Override
    // public void afterForward(WebDriver.Navigation navigation, WebDriver driver) {
    //      System.out.println("<<<<< DEBUG UiTraceListener: afterForward triggered >>>>>");
    //     recordNavigationEvent("navigateForward", this.originalDriver.getCurrentUrl());
    // }

    // @Override
    // public void afterRefresh(WebDriver.Navigation navigation, WebDriver driver) {
    //     System.out.println("<<<<< DEBUG UiTraceListener: afterRefresh triggered >>>>>");
    //     recordNavigationEvent("navigateRefresh", this.originalDriver.getCurrentUrl());
    // }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        Throwable targetException = e.getTargetException();
        System.err.println("<<<<< UiTraceListener: onError triggered. Method: " + method.getName() + 
                           ", Exception: " + targetException.getClass().getName() + 
                           ", Message: " + targetException.getMessage() + " >>>>>");

        // Attempt to filter out NoSuchElementExceptions that might be part of WebDriverWait polling
        // This is a heuristic. If the method is "findElement" and it's a NoSuchElementException,
        // we might choose not to log it to keep the trace cleaner, or log it differently.
        // For now, we will still log it but this is a place for future refinement.
        boolean isLikelyPollingError = method.getName().equals("findElement") && (targetException instanceof NoSuchElementException);

        if (isLikelyPollingError) {
            System.err.println("<<<<< UiTraceListener: onError - Detected likely polling error for findElement. Still logging for now. >>>>>");
        }

        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("eventType", "exceptionInListener");
        event.put("targetClass", target != null ? target.getClass().getName() : "null");
        event.put("methodName", method.getName());
        if (args != null && args.length > 0 && args[0] instanceof By) {
            event.put("locator", args[0].toString());
        }
        event.put("exceptionType", targetException.getClass().getName());
        event.put("errorMessage", targetException.getMessage());
        // Do not include full stack trace in JSON by default, can make it very large
        // event.put("stackTrace", Arrays.toString(targetException.getStackTrace())); 
        
        String stateIdStr = "N/A (stateId not resolved during error)";
        try {
            stateIdStr = getPageStateId(this.originalDriver);
        } catch (Exception pageStateException) {
            System.err.println("Exception during getPageStateId in onError: " + pageStateException.getMessage());
        }
        event.put("stateId", stateIdStr);
        
        this.traceEvents.add(event);
        System.err.println("<<<<< UiTraceListener: Exception Event added to traceEvents. Current list size: " + this.traceEvents.size() + " >>>>>");
    }


    public void saveTrace() {
        System.out.println("<<<<< UiTraceListener: Attempting to save trace. Number of events: " + this.traceEvents.size() + " >>>>>");
        if (this.outputFilePath == null || this.outputFilePath.trim().isEmpty()) {
            System.err.println("Output file path for UI trace is not set. Trace not saved.");
            return;
        }
        if (this.traceEvents.isEmpty()) {
            System.out.println("<<<<< UiTraceListener: No events recorded. Trace file will be empty or not created if it only contains an empty list. >>>>>");
            // Optionally, create an empty JSON array file
            // try (FileWriter writer = new FileWriter(this.outputFilePath)) {
            //     writer.write("[]");
            // } catch (IOException e) { /* ignore */ }
            // return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(this.outputFilePath)) {
            gson.toJson(this.traceEvents, writer);
            System.out.println("<<<<< UiTraceListener: UI trace successfully saved to " + this.outputFilePath + " with " + this.traceEvents.size() + " events. >>>>>");
        } catch (IOException e) {
            System.err.println("<<<<< UiTraceListener: Failed to save UI trace to " + this.outputFilePath + " >>>>>");
            e.printStackTrace();
        }
    }
}