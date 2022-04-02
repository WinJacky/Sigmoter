package main.resources.iReader;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

// From V7.9.0
// Bookshelf -> (Search) -> sendKeys("werewolf") -> The Werewolf -> (The Werewolf) -> + Add to shelf
public class AddNovelToShelfTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.zhangyue.read");
        desiredCapabilities.setCapability("appActivity", "com.zhangyue.read.ui.activity.WelcomeActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementByXPath("//android.widget.FrameLayout[@content-desc=\"Bookshelf\"]/android.view.ViewGroup/android.widget.TextView").click();
        driver.findElementById("com.zhangyue.read:id/bookshelf_head_search_icon").click();
        driver.findElementById("com.zhangyue.iReader.search:id/searching_main_view__edit_text").sendKeys("werewolf");
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.RelativeLayout[1]/android.widget.TextView").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.ListView/android.widget.FrameLayout[1]/android.widget.RelativeLayout/android.view.View").click();
        driver.findElementById("com.zhangyue.read:id/book_detail_add_bookshelf").click();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
}
