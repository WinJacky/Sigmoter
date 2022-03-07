package main.resources.iReader;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V7.9.0
// (Add) -> Import local files -> MY DIRECTORY -> (Sort) -> By Size
public class ImportBookTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.zhangyue.read");
        desiredCapabilities.setCapability("appActivity", "com.zhangyue.read.ui.activity.WelcomeActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.zhangyue.read:id/bookshelf_head_add_icon").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.TextView[2]").click();
        driver.findElementByXPath("//android.support.v7.app.ActionBar.Tab[@content-desc=\"My directory\"]/android.widget.TextView").click();
        driver.findElementById("com.zhangyue.read:id/title_sort_icon").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.ScrollView/android.widget.LinearLayout/android.widget.FrameLayout[3]/android.widget.RadioButton").click();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
}
