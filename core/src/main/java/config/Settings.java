package main.java.config;

import java.io.File;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:02
 */
public class Settings {

    public String appName;
    public String testSuite;
    // 旧版应用元素信息抽取存储文件夹
    public String referenceInfoExtractionFolder;

    public Settings(AppEnum app) {
        this.appName = app.getAppName();
        this.testSuite = app.getTestSuite();
        this.referenceInfoExtractionFolder = outputPath + sep + testSuite.substring(testSuite.lastIndexOf(".") + 1) + sep;
    }

    public static String sep = File.separator;
    public static String outputPath = "output";

    public static String extractInfoPath = "AppInfo";
    public static String repairedTCPath = "RepairedTC";

    public static final String XML_EXT = ".xml";
    public static final String JSON_EXT = ".json";
    public static final String JAVA_EXT = ".java";

    public static boolean aspectActive = false;
}
