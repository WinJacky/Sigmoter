package main.java.config;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:04
 */
public enum AppEnum {

    AlarmMon("AlarmMon", "main.resources.AlarmMon"),

    BBCNews("BBCNews", "main.resources.BBCNews"),

    DaysMatter("DaysMatter", "main.resources.DaysMatter"),

    FotMob("FotMob", "main.resources.FotMob"),

    HryFine("HryFine", "main.resources.HryFine"),

    iReader("iReader", "main.resources.iReader"),

    LarkPlayer("LarkPlayer", "main.resources.LarkPlayer"),

    Notepad("Notepad", "main.resources.Notepad"),

    SenseWeather("SenseWeather", "main.resources.SenseWeather"),

    Webtoon("Webtoon", "main.resources.Webtoon"),

    APIDemo("APIDemo", "main.resources.APIDemo");

    private String appName;

    // 旧版应用测试用例集存放包名
    private String testSuite;

    AppEnum(String appName, String testSuite) {
        this.appName = appName;
        this.testSuite = testSuite;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(String testSuite) {
        this.testSuite = testSuite;
    }
}
