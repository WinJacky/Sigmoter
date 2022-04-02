package iReader;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SortNovelInShelfTest {

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
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementByXPath("//android.widget.TextView[@text=\"Bookshelf\"]").click();
        driver.findElementById("com.zhangyue.read:id/bookshelf_head_add_icon").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Book Sorting\"]").click();
        driver.findElementByXPath("//android.widget.RadioButton[@text=\"By Alphabet\"]").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
