package HryFine;

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

public class YesterdayStepTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.lianhezhuli.hyfit");
        desiredCapabilities.setCapability("appActivity", "com.lianhezhuli.hyfit.SplashActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.lianhezhuli.hyfit:id/main_tab_health_rbtn").click();
        new TouchAction(driver).press(PointOption.point(380, 880)).moveTo(PointOption.point(380, 360)).release().perform();
        driver.findElementByXPath("//android.widget.TextView[@resource-id='com.lianhezhuli.hyfit:id/item_home_tab_description_tv' and @text=\"Sports\"]").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Today's steps\"]").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/left_data_iv").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
