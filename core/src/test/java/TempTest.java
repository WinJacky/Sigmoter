package test.java;


import com.google.gson.Gson;
import main.java.config.Settings;
import main.java.runner.RepairRunner;
import main.java.util.UtilsRepair;
import org.openqa.selenium.Point;

import java.io.File;
import java.io.Serializable;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/6 17:00
 */
public class TempTest {

    public static void main(String[] args) throws Exception {
        /*
        AppEnum appEnum = AppEnum.LarkPlayer;
        String testcaseName = "SettingTest";
        String pathToTestBroken = UtilsFileGetter.getTestFile(testcaseName, ("core.src." + appEnum.getTestSuite()).replaceAll("\\.", "\\\\"));

        ParseTest pt = new ParseTest(Settings.outputPath + Settings.sep + Settings.repairedTCPath + Settings.sep + appEnum.getAppName());

        EnhancedTestCase testBroken = pt.parseAndSerialize(pathToTestBroken);
        EnhancedTestCase testRepaired = pt.parseAndSerialize(UtilsFileGetter.getTestFile(testcaseName, pathToTestBroken.substring(0, pathToTestBroken.lastIndexOf("\\"))));

        System.out.println(pathToTestBroken);
        System.out.println(UtilsFileGetter.getTestFile(testcaseName, pathToTestBroken.substring(0, pathToTestBroken.lastIndexOf("\\"))));
        */
        /*
        String newPath = "D:\\Study\\IDEA\\IDEAWorkspace\\Sigmoter\\output\\RepairedTC\\LarkPlayer\\SettingTest.java";
        String prefix = newPath.replace(System.getProperty("user.dir"),"");
        prefix = prefix.substring(1, prefix.lastIndexOf("\\"));
        prefix = prefix.replace("\\", ".");
        System.out.println(prefix);
         */
        /*
        String tempStr = "desiredCapabilities.setCapability(\"ensureWebviewsHavePages\", true);";
        String[] keyValue = StringUtils.substringsBetween(tempStr, "(", ")")[0].split(", ");
        System.out.println(keyValue[0]+"__"+keyValue[1]);
         */
        /*
        EnhancedNavigate navigate = new EnhancedNavigate(21, "back");

        Statement s1 = navigate;
        System.out.println(s1.toString());

        EnhancedTouchAction touchAction = new EnhancedTouchAction(22, "swipe");
        touchAction.setStartPoint(new Point(321, 244));
        touchAction.setEndPoint(new Point(321, 122));

        Statement s2 = touchAction;
        System.out.println(s2.toString());
         */
        /*
        String xpath2 = "//android.widget.TextView[@content-desc='NFC'];//android.widget.TextView[@text='NFC'];//android.widget.ListView[@resource-id='android:id/list']/android.widget.TextView[8];//hierarchy/android.widget.FrameLayout[1]/android.view.ViewGroup[1]/android.widget.FrameLayout[2]/android.widget.ListView[1]/android.widget.TextView[8]";
        xpath2 = xpath2.substring(xpath2.indexOf("//hierarchy"));
        System.out.println(xpath2);

        String xpath1 = "//hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[4]/android.widget.TextView";
        xpath2 = "//hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.RelativeLayout[4]/android.widget.TextView";
        System.out.println(UtilsSimilarity.simOfXpath(xpath1, xpath2));
         */
        /*
        System.out.println(isAbbreviation("user", "usr"));
        System.out.println(isAbbreviation("usrname", "usr"));
        System.out.println(isAbbreviation("username", "usr"));
        System.out.println(isAbbreviation("user", "usrname"));
        System.out.println(isAbbreviation("user", "usrnamer"));
         */
        String tempXmlSavedFolder = Settings.repairedTCPath + Settings.sep + "TempXmlSaved";
        File tempFolder = new File(tempXmlSavedFolder);
        String[] content = tempFolder.list();
        for(String name : content) {
            File temp = new File(tempXmlSavedFolder + Settings.sep + name);
            if(!temp.delete()) {
                System.out.println("Failed to delete " + name);
            }
        }
    }

    public static boolean isAbbreviation(String word1, String word2) {
        if (word1.length() > word2.length()) {
            String temp = word1;
            word1 = word2;
            word2 = temp;
        }

        int index1=0, index2=0;
        for (;index1 < word1.length();index1++) {
            while (index2 < word2.length() && word1.charAt(index1)!=word2.charAt(index2)) {
                index2++;
            }
            if (index2 >= word2.length()) {
                break;
            }
        }

        if (index1 == word1.length()) return true;
        return false;
    }
}

//class Animal implements Serializable {
//    public void eat() {
//        System.out.println("Animal is eating");
//    }
//}
//
//class Cat extends Animal {
//    private Point point;
//
//    public Point getPoint() {
//        return point;
//    }
//
//    public void setPoint(Point point) {
//        this.point = point;
//    }
//}