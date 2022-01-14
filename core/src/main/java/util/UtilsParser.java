package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 10:13
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.config.Settings;
import main.java.dataType.EnhancedTestCase;
import main.java.dataType.Statement;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    // 以 JSON 格式保存测试用例
    public static void serializeTestCase(EnhancedTestCase tc, String path, String folder) {
        //TODO
        int lastSlash = path.lastIndexOf("\\");
        int end = path.indexOf(".java");
        String testName = path.substring(lastSlash + 1, end);
        String newPath = folder + testName + Settings.sep + testName + Settings.JSON_EXT;

        try {
            FileUtils.write(new File(newPath), gson.toJson(tc));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sanityCheck(EnhancedTestCase etc, EnhancedTestCase testCorrect) {

        // TODO
        Map<Integer, Statement> a = etc.getStatements();
        Map<Integer, main.java.dataType.Statement> b = testCorrect.getStatements();

        ArrayList<Integer> list1 = new ArrayList<Integer>(a.keySet());
        ArrayList<Integer> list2 = new ArrayList<Integer>(b.keySet());

        if (list1.get(0) != list2.get(0)) {
            logger.error("Tests numbering is not aligned: " + list1.get(0) + "!=" + list2.get(0));
            throw new NullPointerException();
        }

    }

}
