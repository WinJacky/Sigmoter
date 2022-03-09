package AlarmMon;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ServiceInfoTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.malangstudio.alarmmon");
        desiredCapabilities.setCapability("appActivity", "com.malangstudio.alarmmon.SplashActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout[4]/android.widget.ImageView").click();
        driver.findElementByXPath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.malangstudio.alarmmon:id/settingsRecyclerView']/android.widget.RelativeLayout[8]/android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.view.View[1]").click();
        driver.findElementById("com.malangstudio.alarmmon:id/privacyTextView").click();
        driver.findElementByXPath("//android.widget.RelativeLayout[@resource-id='com.malangstudio.alarmmon:id/arrowBackButton']/android.view.View[1]").click();
        driver.findElementById("com.malangstudio.alarmmon:id/oslTextView").click();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        new TouchAction(driver).press(PointOption.point(384, 1056)).moveTo(PointOption.point(384, 288)).release().perform();
        driver.findElementByXPath("//android.widget.TextView[@text=\"androidx.legacy:legacy-support-v4\"]").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
