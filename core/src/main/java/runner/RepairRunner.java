package main.java.runner;

import com.alibaba.fastjson.JSONObject;
import main.java.config.AppEnum;
import main.java.config.Settings;
import main.java.core.Word2Vec;
import main.java.dataType.EnhancedTestCase;
import main.java.dataType.KeyText;
import main.java.dataType.Keyword;
import main.java.domain.WordEntry;
import main.java.util.ParseTest;
import main.java.util.UtilsFileGetter;
import main.java.util.UtilsParser;
import main.java.utils.WordsSplit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:00
 */
public class RepairRunner {

    private static Logger logger = LoggerFactory.getLogger(RepairRunner.class);

    private static AppEnum appEnum;

    private static long startTime;

    private static long stopTime;

    private static long elapsedTime;

    // nlp
    private static List<Keyword> keywordList = new ArrayList<>();

    public static Word2Vec word2Vec = null;

    public static void main(String[] args) {
        RepairRunner repairRunner = new RepairRunner();
        // 待修复用例配置
        appEnum = AppEnum.LarkPlayer;
        String testcaseName = "SettingTest";
        // 读取 Word2Vector 模型
        word2Vec = new Word2Vec("nlp/src/main/resources/GloVe/glove.6B.100d.txt");

        // 读取测试用例关键词序列，分词、扩词
        List<KeyText> keyTextList = readKeyText(appEnum, testcaseName);
        for (KeyText text: keyTextList) {
            Keyword temp = new Keyword(text.getLineNumber(), WordsSplit.getWords(text.getText()));
            temp.addToExtendSeq(extendKeywords(temp.getKeywords(), 5));
            keywordList.add(temp);
        }

        // 开始修复
        repairRunner.startRepair(testcaseName);
    }

    public static List<KeyText> readKeyText(AppEnum appEnum, String testCaseName) {
        String appName = appEnum.getAppName();
        String fileName = Settings.outputPath + Settings.sep + Settings.extractInfoPath + Settings.sep + appName + Settings.sep + testCaseName + "-keyTextListInfo" + Settings.JSON_EXT;
        File file = new File(fileName);
        List<KeyText> result = null;

        try {
            String content = FileUtils.readFileToString(file, Charset.defaultCharset());
            result = JSONObject.parseArray(content, KeyText.class);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> extendKeywords(List<String> words, int num) {
        List<String> keywordSeq = new ArrayList<>();
        for (String str: words) {
            Set<WordEntry> set = word2Vec.getTopNSimilarWords(str, num);
            for (WordEntry entry: set) {
                keywordSeq.add(entry.name);
            }
            keywordSeq.add(str);
        }
        return keywordSeq;
    }

    private void startRepair(String testcaseName) {
        // 开始计时
        startTime = System.currentTimeMillis();

        // 获取待修复测试用例绝对路径
        String pathToTestBroken = UtilsFileGetter.getTestFile(testcaseName, ("core.src." + appEnum.getTestSuite()).replaceAll("\\.", "\\\\"));
        logger.info("Verifying test " + appEnum.getTestSuite() + "." + testcaseName);

        // parse test case
        ParseTest pt = null;
        EnhancedTestCase testBroken = null;
        EnhancedTestCase testRepaired = null;

        try {
            //TODO
            pt = new ParseTest(Settings.outputPath + Settings.sep + Settings.repairedTCPath + Settings.sep + appEnum.getAppName());
            testBroken = pt.parseAndSerialize(pathToTestBroken);
            testRepaired = pt.parseAndSerialize(UtilsFileGetter.getTestFile(testcaseName, pathToTestBroken.substring(0, pathToTestBroken.lastIndexOf("\\"))));
            logger.info("*************************************************");
            logger.info(UtilsFileGetter.getTestFile(testcaseName, pathToTestBroken.substring(0, pathToTestBroken.lastIndexOf("\\"))));
            logger.info(pathToTestBroken);
            logger.info("*************************************************");
            UtilsParser.sanityCheck(testBroken, testRepaired);
        } catch (NullPointerException e) {
            e.printStackTrace();
            logger.error("Errors occurred while initializing the test case. " + "Verify that the settings are correct and the test case starts with the same line number.");
            // clean up
            shutDown();
            System.exit(1);
        }

        // 修复时间结算
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        logger.info("Repair spends {} s.", String.format("%.3f", elapsedTime / 1000.0f));
    }

    // 处理退出信息
    private void shutDown() {

    }
}
