package main.resources.FotMob;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V78.0.5104
// Search -> sendKeys("Barcelona")
public class SearchBarcelonaTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.mobilefootie.wc2010");
        desiredCapabilities.setCapability("appActivity", "com.mobilefootie.fotmob.gui.v2.MainActivityWrapper");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        driver.findElementByAccessibilityId("Search").click();
        driver.findElementById("com.mobilefootie.wc2010:id/editText_search").sendKeys("Barcelona");
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
}
