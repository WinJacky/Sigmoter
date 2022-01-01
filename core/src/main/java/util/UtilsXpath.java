package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/7/5 11:20
 */

import io.appium.java_client.MobileElement;
import org.openqa.selenium.JavascriptExecutor;

/**
 * This class refers to the same class in Vista
 */
public class UtilsXpath {
    /**
     * Given an Mobile element, retrieve its XPath.
     *
     * @param js
     *      Selenium JavaScriptExecutor object to execute javascript.
     * @param element
     *      Selenium WebElement corresponding to the HTML element.
     * @return XPath of the given element.
     */
    public static String getElementXPath(JavascriptExecutor js, MobileElement element) {
        return (String) js.executeScript(
                "var getElementXPath = function(element) {" +
                        "return getElementTreeXPath(element);" +
                   "};" +
                   "var getElementTreeXPath = function(element) {" +
                        "return element;" +
                       /*
                       "var paths = [];" +
                       "for (; element && element.nodeType == 1; element = element.parentNode) {" +
                           "var index = 0;" +
                           "for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {" +
                               "if (sibling.nodeType == Node.DOCUMENT_TYPE_NODE) {" +
                                   "continue;" +
                               "}" +
                               "if (sibling.nodeName == element.nodeName) {" +
                                   "++index;" +
                               "}" +
                           "}" +
//                           "var className = element.className.toLowerCase();" +
                           "var pathIndex = (\"[\" + (index+1) + \"]\");" +
                           "paths.splice(0, 0, pathIndex);" +
//                           "paths.splice(0, 0, className + pathIndex);" +
                       "}" +
                       "return paths.length ? \"//\" + paths.join(\"/\") : null;" +
                         */
                   "};" +
                   "return getElementXPath(arguments[0]);", element
        );
    }
}
