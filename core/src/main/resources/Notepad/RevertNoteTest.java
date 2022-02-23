package main.resources.Notepad;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V3.2
// Hello,how are you? -> sendKeys("SEU") -> REVERT -> REVERT
public class RevertNoteTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.atomczak.notepat");
        desiredCapabilities.setCapability("appActivity", "com.atomczak.notepat.MainActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.atomczak.notepat:id/note_list_title_text").click();
        driver.findElementById("com.atomczak.notepat:id/textNoteTitleEdit").clear();
        driver.findElementById("com.atomczak.notepat:id/textNoteTitleEdit").sendKeys("SEU");
        driver.findElementById("com.atomczak.notepat:id/action_revert_changes").click();
        driver.findElementById("android:id/button1").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
