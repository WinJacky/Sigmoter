package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 10:13
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.config.Settings;
import main.java.dataType.AppiumLocator;
import main.java.dataType.EnhancedTestCase;
import main.java.dataType.EnhancedTouchAction;
import main.java.dataType.Statement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class refers to the class in Vista
 */
public class UtilsParser {

    private static Logger logger = LoggerFactory.getLogger(UtilsParser.class);

    static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static String getTestSuiteNameFromWithinType(String withinType) {

        if (withinType.contains("main.java")) {
            withinType = withinType.replaceAll("main.java.", "");
        } else if(withinType.contains("main.resources")) {
            withinType = withinType.replaceAll("main.resources.", "");
        }

        withinType = withinType.replaceAll("class ", "");
        withinType = withinType.substring(0, withinType.indexOf("."));
        return withinType;
    }

    /**
     * 以 JSON 形式保存旧测试用例
     * @param tc 经过 MethodVisitor 处理过后的旧测试用例
     * @param path 旧测试用例绝对路径，用来提取测试用例名称
     * @param folder 想要保存的文件夹路径
     */
    public static void serializeTestCase(EnhancedTestCase tc, String path, String folder) {
        int lastSlash = path.lastIndexOf("\\");
        int end = path.indexOf(".java");
        String testName = path.substring(lastSlash + 1, end);
        String newPath = folder + Settings.sep + testName + Settings.JSON_EXT;

        try {
            FileUtils.write(new File(newPath), gson.toJson(tc), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从测试用例语句 s 中获取定位策略和对应的值
    public static AppiumLocator getAppiumLocator(String s) throws Exception {
        String[] strategyAndValue = StringUtils.substringsBetween(s, "findElementBy", ")");
        if(strategyAndValue.length != 1) {
            throw new Exception("[ERR]\tdriver findElement statement malformed");
        }
        String[] splitted = StringUtils.split(strategyAndValue[0], "(");

        String strategy = splitted[0].trim().toLowerCase();
        if (strategy.equals("id")) {
            strategy = "resourceId";
        } else if (strategy.equals("accessibilityid")) {
            strategy = "contentDesc";
        }
        String value = splitted[1].replaceAll("\"", "").trim();

        return new AppiumLocator(strategy, value);
    }

    // 从测试用例语句 s 中获取 sendKeys对应的值
    public static String getValueFromSendKeys(String s) throws Exception {
        String[] valuesInQuotes = StringUtils.substringsBetween(s, "sendKeys(\"", "\")");

        if (valuesInQuotes.length != 1) {
            throw new Exception("[ERR]\\tSendKeys statement malformed");
        }

        return valuesInQuotes[0];
    }

    // 从测试用例语句 s 中获取滑动操作对应的起始和终止点位
    public static EnhancedTouchAction getSwipePoints(EnhancedTouchAction action, String s) throws Exception {
        String[] points = StringUtils.substringsBetween(s, "PointOption.point(", ")");

        if (points.length != 2) {
            throw new Exception("[ERR]\\tSwipe statement malformed");
        }
        String[] splitted = StringUtils.split(points[0], ",");
        Point point = new Point(Integer.parseInt(splitted[0].trim()), Integer.parseInt(splitted[1].trim()));
        action.setStartPoint(point);
        splitted = StringUtils.split(points[1], ",");
        point = new Point(Integer.parseInt(splitted[0].trim()), Integer.parseInt(splitted[1].trim()));
        action.setEndPoint(point);

        return action;
    }
}
