package main.resources.HryFine;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

// From V2.1.5
// (My) -> (Avatar) -> Nickname -> clear() -> sendKeys("SEU") -> determine
public class ModifyNicknameTest {

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
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.lianhezhuli.hyfit:id/main_tab_my_img").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/iv_head").click();
        driver.findElementByXPath("//hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[2]/android.widget.RelativeLayout[1]/android.widget.TextView[1]").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/et_content").clear();
        driver.findElementById("com.lianhezhuli.hyfit:id/et_content").sendKeys("SEU");
        driver.findElementById("com.lianhezhuli.hyfit:id/btn_right").click();
    }
    
    @After
    public void tearDown() {
        driver.quit();
    }
}
