package main.java.config;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:04
 */
public enum AppEnum {

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
