package Webtoon;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class CompletedComicTest {

    private AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName", "Android");
        desiredCapabilities.setCapability("platformVersion", "11");
        desiredCapabilities.setCapability("deviceName", "Android Emulator");
        desiredCapabilities.setCapability("automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appPackage", "com.naver.linewebtoon");
        desiredCapabilities.setCapability("appActivity", "com.naver.linewebtoon.splash.SplashActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.naver.linewebtoon:id/daily_tab").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"COMPLETED\"]").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Sort by Popularity\"]").click();
        driver.findElementByXPath("//android.widget.TextView[@text='Sort by Popularity']").click();
        driver.findElementByXPath("//androidx.recyclerview.widget.RecyclerView[@resource-id='com.naver.linewebtoon:id/title_list']/android.view.ViewGroup[5]/android.widget.ImageView[1]").click();
        driver.findElementById("com.naver.linewebtoon:id/available_count").click();
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
