package LarkPlayer;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.dywx.larkplayer");
        desiredCapabilities.setCapability("appActivity", ".gui.MainActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        driver.findElementByAccessibilityId("Navigate up").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Music & Video\"]").click();
        driver.findElementByAccessibilityId("Sort by…").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Length\"]").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
