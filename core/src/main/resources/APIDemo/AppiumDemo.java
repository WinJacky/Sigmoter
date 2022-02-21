/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/22 15:58
 */

package main.resources.APIDemo;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class AppiumDemo {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("app", "D:\\学校事务\\A.毕设\\Android应用\\ApiDemos-debug.apk");
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://0.0.0.0:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        driver.findElementByAccessibilityId("NFC").click();
        driver.findElementByAccessibilityId("TechFilter").click();
        driver.navigate().back();
        driver.navigate().back();
        new TouchAction(driver).press(PointOption.point(399,583)).moveTo(PointOption.point(399,334)).release().perform();
        driver.findElementByAccessibilityId("Views").click();
        driver.findElementByAccessibilityId("Controls").click();
        driver.findElementByAccessibilityId("1. Light Theme").click();
        driver.findElementById("io.appium.android.apis:id/edit").sendKeys("My name is SEU");
        driver.findElementByAccessibilityId("Checkbox 1").click();
        driver.findElementByAccessibilityId("RadioButton 2").click();
        driver.findElementByAccessibilityId("Star").click();
        driver.findElementById("io.appium.android.apis:id/toggle1").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
