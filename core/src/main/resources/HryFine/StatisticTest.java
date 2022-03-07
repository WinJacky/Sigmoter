package main.resources.HryFine;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V2.1.5
// (Swipe) -> Sport -> Statistics -> Month
public class StatisticTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.lianhezhuli.hyfit");
        desiredCapabilities.setCapability("appActivity", "com.lianhezhuli.hyfit.WelcomeActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        new TouchAction(driver).press(PointOption.point(380, 780)).moveTo(PointOption.point(380, 360)).release().perform();
        driver.findElementById("com.lianhezhuli.hyfit:id/home_sport_description_tv").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/sport_record_statistics_tv").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/sport_statistics_indicator_month_rb").click();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
}
