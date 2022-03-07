package main.resources.SenseWeather;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V4.96.51
// (Spinner) -> Shanghai
public class LocationSwitchTest {

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
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.droid27.senseflipclockweather:id/toolbarSpinner").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.RelativeLayout[2]/android.widget.TextView[1]").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
