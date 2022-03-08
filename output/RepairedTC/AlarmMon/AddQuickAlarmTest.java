package AlarmMon;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class AddQuickAlarmTest {

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
        driver.findElementByXPath("//android.view.ViewGroup[@resource-id='com.malangstudio.alarmmon:id/add_floating_action_menu']/android.widget.ImageButton[1]").click();
        driver.findElementById("com.malangstudio.alarmmon:id/add_floating_action_menu_quick").click();
        driver.findElementById("com.malangstudio.alarmmon:id/fiveMinuteButton").click();
        driver.findElementById("com.malangstudio.alarmmon:id/resetButton").click();
        driver.findElementById("com.malangstudio.alarmmon:id/thirtyMinuteButton").click();
        driver.findElementById("com.malangstudio.alarmmon:id/sixtyMinuteButton").click();
        driver.findElementById("com.malangstudio.alarmmon:id/saveButton").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
