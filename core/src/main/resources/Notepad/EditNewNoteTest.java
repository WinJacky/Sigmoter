package main.resources.Notepad;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

// From V1
// New note fab -> sendKeys("SEU") -> sendKeys("I am a student.") -> Save
public class EditNewNoteTest {

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
        driver.findElementById("com.atomczak.notepat:id/new_note_fab").click();
        driver.findElementById("com.atomczak.notepat:id/textNoteTitleEdit").sendKeys("SEU");
        driver.findElementById("com.atomczak.notepat:id/textNoteContentEdit").sendKeys("I am a student.");
        driver.findElementByAccessibilityId("Save").click();
        driver.navigate().back();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
