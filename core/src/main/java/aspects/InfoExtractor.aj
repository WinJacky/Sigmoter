package main.java.aspects;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import main.java.config.Settings;
import main.java.dataType.KeyText;
import main.java.util.UtilsAspect;
import main.java.util.UtilsHierarchyXml;
import main.java.util.UtilsParser;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author feisher
 * @date 2021/6/24 17:01
 * @version 1.0
 */
public aspect InfoExtractor {
    private static Logger logger = LoggerFactory.getLogger("InfoExtractor.aj");

    static AndroidDriver driver;
    static String testFolder;
    static String testCaseName;

    // Statement Info
    static String statement;
    static int statementLine;

    // Hierarchy Layout
    static String hierarchyLayoutXmlFile;

    // ViewTree Info
    static String viewTreeInfoJsonFile;

    // Keyword Info
    static List<KeyText> keyTexts;
    static String keyTextsInfoJsonFile;

    // Intercept the calls to findElement methods
    pointcut logFindElementCalls() : call(* io.appium.java_client.MobileDriver.findElementByAccessibilityId(..))||
                                    call(* io.appium.java_client.MobileDriver.findElementById(..)) ||
                                    call(* io.appium.java_client.MobileDriver.findElementByClassName(..)) ||
                                    call(* io.appium.java_client.MobileDriver.findElementByCssSelector(..)) ||
                                    call(* io.appium.java_client.MobileDriver.findElementByLinkText(..)) ||
                                    call(* io.appium.java_client.MobileDriver.findElementByName(..)) ||
                                    call(* io.appium.java_client.MobileDriver.findElementByXPath(..));

    // Intercept the calls to MobileElement methods
    pointcut logMobileActionCommands() : call(* org.openqa.selenium.WebElement.click()) ||
                                        call(* org.openqa.selenium.WebElement.sendKeys(..)) ||
                                        call(* org.openqa.selenium.WebElement.getText()) ||
                                        call(* org.openqa.selenium.WebElement.clear()) ||
                                        call(* org.openqa.selenium.Alert.accept());

    // Intercept tearDown() method
    pointcut logTearDownCommand() : call(* org.openqa.selenium.remote.RemoteWebDriver.quit());

    // Create output folder of current test case before calling the methods
    // eg: output/AppInfo/ADDR/TC1
    before() : logFindElementCalls() {
        if(Settings.aspectActive && driver == null) {
            logger.info("Execute logFindElementCalls...");

            driver = (AndroidDriver) thisJoinPoint.getTarget();
            String withinType = thisJoinPoint.getSourceLocation().getWithinType().toString();
            String testSuiteName = UtilsParser.getTestSuiteNameFromWithinType(withinType);

            // 测试用例类名,如：TC1
            testCaseName = thisJoinPoint.getSourceLocation().getFileName().replace(Settings.JAVA_EXT, "");
            testFolder = Settings.extractInfoPath + Settings.sep + testSuiteName + Settings.sep + testCaseName;
            // 例如: output/AppInfo/ADDR/TC1
            UtilsAspect.createTestFolder(testFolder);
        }
    }

    // Save hierarchy layout and view tree information before executing the method
    before() : logMobileActionCommands() {
        if(Settings.aspectActive) {
            logger.info("Execute logMobileActionCommands...");

            MobileElement me = null;
            if (thisJoinPoint.getTarget() instanceof MobileElement) {
                me = (MobileElement) thisJoinPoint.getTarget();
            } else if (thisJoinPoint.getTarget() instanceof Alert) {
                me = null;
            }

            // 获取当前测试语句及其所在行号
            statement = thisJoinPoint.getStaticPart().toString();
            statementLine = thisJoinPoint.getSourceLocation().getLine();
            // 获取当前视图层次布局信息
            hierarchyLayoutXmlFile = testFolder + Settings.sep + statementLine + "-hierarchy" + Settings.XML_EXT;
            UtilsHierarchyXml.takeXmlSnapshot(driver, System.getProperty("user.dir") + Settings.sep + hierarchyLayoutXmlFile);
            // 获取并保存目标元素视图树信息
            viewTreeInfoJsonFile = testFolder + Settings.sep + statementLine + "-viewTreeInfo" + Settings.JSON_EXT;
            UtilsAspect.saveViewTreeInformation(me, hierarchyLayoutXmlFile, viewTreeInfoJsonFile);

            // 获取关键词信息
            String methodName = thisJoinPoint.getSignature().getName();
            String type = null;
            if (methodName.equals("sendKeys")){
                return;
            } else if (methodName.equals("click")) {
                type = "click";
            }
            // 获取并保存当前元素的文本内容，仅限click事件的目标元素
            String textContent = UtilsAspect.getKeyText(thisJoinPoint);
            if (StringUtils.isNotBlank(textContent)) {
                if (keyTexts == null) {
                    keyTexts = new ArrayList<>();
                }
                keyTexts.add(new KeyText(statementLine, textContent, type));
            }
            logger.info("@Before " + statement);
        }
    }

    // Save key text information before tearing down
    before() : logTearDownCommand() {
        if (keyTexts != null && keyTexts.size() != 0) {
            keyTextsInfoJsonFile = testFolder + "-keyTextListInfo" + Settings.JSON_EXT;
            UtilsAspect.saveKeyTextInfo(keyTexts, keyTextsInfoJsonFile);
            logger.info("Key Text Info was saved!");
        }
    }
}