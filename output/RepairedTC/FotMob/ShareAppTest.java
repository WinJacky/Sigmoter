package FotMob;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ShareAppTest {

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
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementByXPath("//android.widget.TextView[@text=\"More\"]").click();
        driver.findElementById("com.mobilefootie.wc2010:id/textView_signIn").click();
        driver.findElementById("com.mobilefootie.wc2010:id/button_google").click();
        driver.findElementByXPath("//android.widget.FrameLayout[@resource-id='com.mobilefootie.wc2010:id/card6']/android.widget.ImageView[1]").click();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        driver.findElementById("com.mobilefootie.wc2010:id/txtShare_fotmob").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
