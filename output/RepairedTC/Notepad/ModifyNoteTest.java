package Notepad;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;

public class ModifyNoteTest {

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
        driver.findElementByXPath("//android.widget.TextView[@text=\"Hi, how are you? (tap to open)\"]").click();
        driver.findElementById("com.atomczak.notepat:id/textNoteTitleEdit").clear();
        driver.findElementById("com.atomczak.notepat:id/textNoteTitleEdit").sendKeys("Hello, I am from SEU!");
        driver.findElementById("com.atomczak.notepat:id/textNoteContentEdit").clear();
        driver.findElementById("com.atomczak.notepat:id/textNoteContentEdit").sendKeys("My name is Feisher.");
        driver.findElementById("com.atomczak.notepat:id/action_save_note").click();
        driver.navigate().back();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
