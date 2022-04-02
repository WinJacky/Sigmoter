package SenseWeather;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class SettingTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.droid27.senseflipclockweather");
        desiredCapabilities.setCapability("appActivity", "com.droid27.senseflipclockweather.LauncherActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementByAccessibilityId("Settings").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Setup general appearance settings\"]").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Weather icons style\"]").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.RelativeLayout/android.widget.LinearLayout[2]/android.widget.ListView/android.widget.RelativeLayout[3]/android.widget.LinearLayout/android.widget.TextView").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Display moon phase\"]").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
