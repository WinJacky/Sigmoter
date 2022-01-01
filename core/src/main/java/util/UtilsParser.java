package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 10:13
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class refers to the class in Vista
 */
public class UtilsParser {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

}
