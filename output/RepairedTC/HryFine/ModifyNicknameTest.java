package HryFine;

import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
        desiredCapabilities.setCapability("appActivity", "com.lianhezhuli.hyfit.SplashActivity");
        desiredCapabilities.setCapability("noReset", true);
        desiredCapabilities.setCapability("ensureWebviewsHavePages", true);
        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void sampleTest() {
        driver.findElementById("com.lianhezhuli.hyfit:id/main_tab_my_rbtn").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/fragment_my_head_img").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/account_nickname_tv").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"My information\"]").click();
        driver.findElementByXPath("//android.widget.TextView[@text=\"Nickname\"]").click();
        driver.findElementById("com.lianhezhuli.hyfit:id/qmui_dialog_edit_input").clear();
        driver.findElementById("com.lianhezhuli.hyfit:id/qmui_dialog_edit_input").sendKeys("SEU");
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
