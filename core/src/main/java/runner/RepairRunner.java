package main.java.runner;

import com.alibaba.fastjson.JSONObject;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import main.java.config.AppEnum;
import main.java.config.Settings;
import main.java.config.Threshold;
import main.java.core.Word2Vec;
import main.java.dataType.*;
import main.java.domain.WordEntry;
import main.java.util.*;
import main.java.utils.WordsSplit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/24 17:00
 */
public class RepairRunner {

    private static Logger logger = LoggerFactory.getLogger(RepairRunner.class);
    // 修复一条语句的最大探索时间为 30s
    private static final long MAX_EXPLORE_TIME = 30 * 1000;
    // 最大探索深度为 3，当探索超过 3 个状态时需要回退
    private static final int MAX_EXPLORE_DEPTH = 3;

    private static AppEnum appEnum;

    private static long startTime;

    private static long stopTime;

    private static long elapsedTime;

    private static final String remoteUrl = "http://127.0.0.1:4723/wd/hub";

    private static AndroidDriver driver;

    // nlp
    public static List<Keyword> keywordList = new ArrayList<>();
    public static Word2Vec word2Vec = null;
    public static boolean isIdConsidered;

    // 崩溃、元素崩溃、元素修复、删除的语句个数
    private static int brokenStmNum;
    private static int eleBrokenNum;
    private static int eleRepairedNum;
    private static int deletedStmNum;

    // 修复过程需要获取层次布局信息，此文件夹用于临时存储，修复结束需要清理
    public static String tempXmlSavedFolder;
    // 当前屏幕的层次布局文件存储路径
    public static String curLayoutXmlFile;

    public static void main(String[] args) {
        RepairRunner repairRunner = new RepairRunner();
        // 待修复用例配置
        appEnum = AppEnum.LarkPlayer;
        String testcaseName = "AboutTest";
        brokenStmNum = 0;
        eleBrokenNum = 0;
        eleRepairedNum = 0;
        deletedStmNum = 0;

        tempXmlSavedFolder = Settings.repairedTCPath + Settings.sep + "TempXmlSaved";
        // 例如: output/RepairedTC/TempXmlSaved
        UtilsAspect.createTestFolder(tempXmlSavedFolder);

        // 读取 Word2Vector 模型
        word2Vec = new Word2Vec("nlp/src/main/resources/GloVe/glove.6B.100d.txt");

        // 读取测试用例关键词序列，分词、扩词
        List<KeyText> keyTextList = readKeyText(appEnum, testcaseName);
        for (KeyText text: keyTextList) {
            Keyword temp = new Keyword(text.getLineNumber(), WordsSplit.getWords(text.getText()));
            temp.addToExtendSeq(extendKeywords(temp.getKeywords(), 3));
            keywordList.add(temp);
        }

        // 开始修复
        repairRunner.startRepair(testcaseName);
    }

    public static List<KeyText> readKeyText(AppEnum appEnum, String testCaseName) {
        String appName = appEnum.getAppName();
        String fileName = Settings.extractInfoPath + Settings.sep + appName + Settings.sep + testCaseName + "-keyTextListInfo" + Settings.JSON_EXT;
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

    // 关键文本分词得到的关键词列表，其中的每个词都取 num 个语义相关词
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
        // 获取待修复测试用例绝对路径
        String pathToTestBroken = UtilsFileGetter.getTestFile(testcaseName, ("core.src." + appEnum.getTestSuite()).replaceAll("\\.", "\\\\"));
        logger.info("Verifying test " + appEnum.getTestSuite() + "." + testcaseName);

        // parse test case
        ParseTest pt = null;
        EnhancedTestCase testBroken = null;

        pt = new ParseTest(Settings.extractInfoPath + Settings.sep + appEnum.getAppName());
        // 将崩溃测试用例解析到上一步初始化 pt 给出的文件夹中
        testBroken = pt.parseAndSerialize(pathToTestBroken);

        // 上一步解析后得到配置信息
        DesiredCapabilities capabilities = pt.getCapabilities();
        URL url = null;
        try {
            url = new URL(remoteUrl);
        } catch (MalformedURLException e) {
            logger.error("Url is incorrect, please check!");
        }
        driver = new AndroidDriver(url, capabilities);
        // 运行APP
        driver.launchApp();
        while(driver.getPageSource().contains("Test Ad")) {
            logger.error("Please remove ads manually!!");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.info("Driver wait has been interrupted");
            }
        }
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.findElementByClassName("android.widget.TextView");

        /* Map of the original statements. */
        Map<Integer, Statement> originStmMap = testBroken.getStatements();
        /* Map of the repaired statements. */
        Map<Integer, Statement> repairedStmMap = new LinkedHashMap<>();
        /* Map of the repaired path. */
        Map<Integer, List<EnhancedMobileElement>> repairedPathMap = new LinkedHashMap<>();
        Map<Integer, List<EnhancedTouchAction>> repairedSwipeMap = new LinkedHashMap<>();

        boolean noSuchElement;
        // 开始计时
        startTime = System.currentTimeMillis();

        /* For each statement */
        int keywordPos = 0;
        Statement previousStatement = null;
        for (int statementNum : originStmMap.keySet()) {
            noSuchElement = true;
            Statement statement = originStmMap.get(statementNum);
            logger.info("Check statement " + statementNum + ": " + statement.toString());
            MobileElement elementFromAppiumLocator = null;
            List<EnhancedMobileElement> repairedPath = new ArrayList<>();
            List<EnhancedTouchAction> repairedSwipe = new ArrayList<>();
            Statement repairedStm = UtilsRepair.deepClone(statement);

            MobileElement candidateElement = null;

            if (statement.getAction().equals(AppiumAction.Navigate)) {
                previousStatement = statement;
                repairedStmMap.put(statementNum, repairedStm);

                // 执行页面状态转移
                if (statement.getValue().equals("back")) {
                    driver.navigate().back();
                } else if (statement.getValue().equals("forward")) {
                    driver.navigate().back();
                } else if (statement.getValue().equals("refresh")) {
                    driver.navigate().refresh();
                }
                continue;
            }

            if (statement.getAction().equals(AppiumAction.Touch)) {
                previousStatement = statement;
                repairedStmMap.put(statementNum, repairedStm);

                if (statement.getValue().equals("swipe")) {
                    // 执行页面滑动
                    Point startPoint = ((EnhancedTouchAction)repairedStm).getStartPoint();
                    Point endPoint = ((EnhancedTouchAction)repairedStm).getEndPoint();
                    new TouchAction(driver).press(PointOption.point(startPoint)).moveTo(PointOption.point(endPoint)).release().perform();
                } else if (statement.getValue().equals("tap")) {
                    // TODO：根据元素位置进行点击，应考虑提取元素信息判断修复
                }
                continue;
            }

            // 移动当前关键词索引
            while (keywordPos < keywordList.size()-1 && keywordList.get(keywordPos).getLineNumber() < statementNum) {
                keywordPos++;
            }

            // 若通过resource-id属性可以唯一定位该元素，则将resource-id值考虑计算语义相似度
            EnhancedMobileElement tempSta = (EnhancedMobileElement) statement;
            if (tempSta.getXpath().contains("//"+tempSta.getClassName()+"[@resource-id='" + tempSta.getResourceId() + "'];")) {
                isIdConsidered = true;
            }
            // 当前元素捕获时对应的布局文件路径
            String oriLayoutXmlFile = Settings.extractInfoPath + Settings.sep + appEnum.getAppName() +
                    Settings.sep + testcaseName + Settings.sep + statementNum + "-hierarchy" + Settings.XML_EXT;
            // 捕获当前屏幕状态的层次布局文件
            curLayoutXmlFile = tempXmlSavedFolder + Settings.sep + System.currentTimeMillis() + Settings.XML_EXT;
            UtilsHierarchyXml.takeXmlSnapshot(driver, curLayoutXmlFile);

            // 检查当前测试语句是否符合测试意图
            // 若可以通过原定位策略定位并达到相似度阈值 或者 上一条语句已经修复过，则直接放入 repairedStmMap
            // 否则调用 repairWithKSG 进行查找，将找到的结果存放在 candidateElement 中，没找到时 candidateElement == null
            try{
                // 先检查元素是否被修复过，对于相同的操作对象，直接使用上一步的修复结果
                if (identicalWithLastStatement(statement, previousStatement)) {
                    repairedStm = UtilsRepair.deepClone(repairedStmMap.get(statementNum - 1));
                    repairedStm.setLine(statementNum);
                    repairedStm.setAction(statement.getAction());
                    repairedStm.setValue(statement.getValue());
                    repairedStmMap.put(statementNum, repairedStm);
                    elementFromAppiumLocator = UtilsRepair.retrieveElementFromAppiumLocator(driver, ((EnhancedMobileElement) repairedStm).getLocator());
                    noSuchElement = false;
                } else {
                    boolean elementFound = false;

                    // 使用旧元素 locator 进行查找
                    elementFromAppiumLocator = UtilsRepair.retrieveElementFromAppiumLocator(driver, ((EnhancedMobileElement) statement).getLocator());
                    // 对元素进行检查
                    if (elementFromAppiumLocator != null && elementFromAppiumLocator.isDisplayed()) {
                        // 先检查元素结构相似度
                        double structSim = UtilsRepair.checkElementByCollectedInfo(curLayoutXmlFile, elementFromAppiumLocator, (EnhancedMobileElement) statement);
                        // 如果相似度达到 ELE_STRUCT_SIM，则认为一样
                        if (structSim >= Threshold.ELE_STRUCT_SIM.getValue()) {
                            elementFound = true;
                        } else {
                            double semanticSim = UtilsRepair.checkElementBySemantic(elementFromAppiumLocator, (EnhancedMobileElement) statement, isIdConsidered);
                            if (semanticSim >= Threshold.ELE_SEMAN_SIM.getValue()) {
                                elementFound = true;
                            } else {
                                double layoutSim = UtilsRepair.checkElementByLayout(elementFromAppiumLocator, (EnhancedMobileElement) statement, driver.manage().window().getSize(), oriLayoutXmlFile, curLayoutXmlFile, isIdConsidered);
                                if (layoutSim >= Threshold.ELE_Layout_SIM.getValue()) {
                                    elementFound = true;
                                }
                            }
                        }
                    }

                    if (elementFound) {
                        // 按原定位策略找到的元素匹配成功，直接放入修复队列
                        repairedStmMap.put(statementNum, repairedStm);
                        noSuchElement = false;
                    } else {
                        // 按原定位策略找到的元素匹配失败
                        logger.info("Direct breakage detected at line " + statementNum);
                        logger.info("Cause: Locating incorrect element by the locator " + ((EnhancedMobileElement)statement).getLocator() + " in the current state");
                        brokenStmNum++;

                        // 在当前界面查找元素
                        candidateElement = UtilsRepair.searchForTargetElementOnState(driver, (EnhancedMobileElement)statement, oriLayoutXmlFile, repairedSwipe, isIdConsidered);
                        if (candidateElement != null) {
                            noSuchElement = false;
                            if (repairedSwipe.isEmpty()) {
                                // 在当前页面直接修复，表明该情况属于元素修复
                                eleBrokenNum++;
                            }
                            logger.info("目标元素在当前界面被找到。。。");
                        } else {
                            // 这条测试语句不是最后一句，观察其后的测试语句是否对元素操作，如果是，那么尝试找到该元素并删除当前测试语句
                            if (statementNum < (Integer) originStmMap.keySet().toArray()[originStmMap.size()-1]) {
                                int nextStmNum = statementNum + 1;
                                Statement nextStm = originStmMap.get(nextStmNum);
                                if (nextStm instanceof EnhancedMobileElement) {
                                    tempSta = (EnhancedMobileElement) nextStm;
                                    if (tempSta.getXpath().contains("//"+tempSta.getClassName()+"[@resource-id='" + tempSta.getResourceId() + "'];")) {
                                        isIdConsidered = true;
                                    }
                                    // 在当前界面查找元素
                                    String nextStmXmlFile = Settings.extractInfoPath + Settings.sep + appEnum.getAppName() +
                                            Settings.sep + testcaseName + Settings.sep + nextStmNum + "-hierarchy" + Settings.XML_EXT;
                                    candidateElement = UtilsRepair.searchForTargetElementOnState(driver, tempSta, nextStmXmlFile, repairedSwipe, isIdConsidered);
                                    if (candidateElement != null) {
                                        if (!repairedSwipe.isEmpty()) {
                                            repairedSwipeMap.put(nextStmNum, repairedSwipe);
                                        }
                                        // 下一条语句在当前界面找到，当前语句被删除
                                        logger.info("当前操作对象被移除，该测试语句将被删除。。。");
                                        repairedStmMap.put(statementNum, null);
                                        deletedStmNum++;
                                        previousStatement = statement;
                                        continue;
                                    }
                                }
                            }

                            candidateElement = repairWithKSG(keywordPos, (EnhancedMobileElement) statement, oriLayoutXmlFile, repairedPath, repairedSwipe);
                            if (candidateElement == null) noSuchElement = true;
                            else noSuchElement = false;
                        }
                    }
                }
            }  catch (RuntimeException e) {
                logger.info("Direct breakage detected at line " + statementNum);
                if (e instanceof NoSuchElementException) {
                    // 按原定位策略无法找到元素
                    logger.info("Cause: Non-selection of element by the locator " + ((EnhancedMobileElement)statement).getLocator() + " in the current state");
                } else {
                    // 按原定位策略无法唯一定位元素
                    logger.info("Cause: Found more than one element by the locator " + ((EnhancedMobileElement)statement).getLocator() + " in the current state");
                }
                brokenStmNum++;

                // 在当前界面查找元素
                candidateElement = UtilsRepair.searchForTargetElementOnState(driver, (EnhancedMobileElement)statement, oriLayoutXmlFile, repairedSwipe, isIdConsidered);
                if (candidateElement != null) {
                    noSuchElement = false;
                    if (repairedSwipe.isEmpty()) {
                        // 在当前页面直接修复，表明该情况属于元素修复
                        eleBrokenNum++;
                    }
                    logger.info("目标元素在当前界面被找到。。。");
                } else {
                    // 这条测试语句不是最后一句，观察其后的测试语句是否对元素操作，如果是，那么尝试找到该元素并删除当前测试语句
                    if (statementNum < (Integer) originStmMap.keySet().toArray()[originStmMap.size()-1]) {
                        int nextStmNum = statementNum + 1;
                        Statement nextStm = originStmMap.get(nextStmNum);
                        if (nextStm instanceof EnhancedMobileElement) {
                            tempSta = (EnhancedMobileElement) nextStm;
                            if (tempSta.getXpath().contains("//"+tempSta.getClassName()+"[@resource-id='" + tempSta.getResourceId() + "'];")) {
                                isIdConsidered = true;
                            }
                            // 在当前界面查找元素
                            String nextStmXmlFile = Settings.extractInfoPath + Settings.sep + appEnum.getAppName() +
                                    Settings.sep + testcaseName + Settings.sep + nextStmNum + "-hierarchy" + Settings.XML_EXT;
                            candidateElement = UtilsRepair.searchForTargetElementOnState(driver, tempSta, nextStmXmlFile, repairedSwipe, isIdConsidered);
                            if (candidateElement != null) {
                                if (!repairedSwipe.isEmpty()) {
                                    repairedSwipeMap.put(nextStmNum, repairedSwipe);
                                }
                                // 下一条语句在当前界面找到，当前语句被删除
                                logger.info("当前操作对象被移除，该测试语句将被删除。。。");
                                repairedStmMap.put(statementNum, null);
                                deletedStmNum++;
                                previousStatement = statement;
                                continue;
                            }
                        }
                    }

                    candidateElement = repairWithKSG(keywordPos, (EnhancedMobileElement) statement, oriLayoutXmlFile, repairedPath, repairedSwipe);
                    if (candidateElement == null) noSuchElement = true;
                    else noSuchElement = false;
                }
            }

            if (noSuchElement) {
                // 没找到修复元素时，这条语句会被删除
                logger.info("Current statement cannot be repaired, it will be deleted from the test");
                repairedStmMap.put(statementNum, null);
                deletedStmNum++;
            } else {
                // 执行事件，状态转移
                if (candidateElement != null) {
                    // 修复
                    AppiumLocator locator = UtilsRepair.getAppropriateLocator(driver, (EnhancedMobileElement) statement, candidateElement, curLayoutXmlFile);
                    // 对于测试用例生成而言，只需要 AppiumLocator 和 AppiumAction 以及对应的 value 值即可
                    ((EnhancedMobileElement)repairedStm).setLocator(locator);
                    // 判断修复后的测试语句与崩溃语句其定位策略是否一样，不一样代表存在元素修复
                    if (!repairedStm.equals(statement)) {
                        eleRepairedNum++;
                    }
                    repairedStmMap.put(statementNum, repairedStm);
                    if (repairedPath.size() > 0) {
                        repairedPathMap.put(statementNum, repairedPath);
                    }
                    if (repairedSwipe.size() > 0) {
                        repairedSwipeMap.put(statementNum, repairedSwipe);
                    }

                    // 执行
                    fireEvent(candidateElement, repairedStm);
                } else if (repairedStmMap.get(statementNum) != null) {
                    // 判断修复后的测试语句与崩溃语句其定位策略是否一样，不一样代表存在元素修复
                    if (!repairedStm.equals(statement)) {
                        eleRepairedNum++;
                    }
                    fireEvent(elementFromAppiumLocator, repairedStm);
                } else {
                    logger.info("Current statement cannot be repaired, it will be deleted from the test");
                    repairedStmMap.put(statementNum, null);
                    deletedStmNum++;
                }
            }
            previousStatement = statement;
        }

        // 修复时间结算
        stopTime = System.currentTimeMillis();
        // 需要刨除点击等待和滑动等待的时间
        elapsedTime = stopTime - startTime - 3000;

        EnhancedTestCase testRepaired = generateNewTest(testBroken, repairedStmMap, repairedPathMap, repairedSwipeMap);
        UtilsRepair.saveTest(pt, testRepaired, testBroken.getPath());

        shutDown(repairedPathMap.size(), repairedSwipeMap.size());
    }

    // 判断当前语句与上一条语句所操作的对象元素是否相同
    private boolean identicalWithLastStatement(Statement statement, Statement previousStatement) {
        if (previousStatement == null) {
            return false;
        }
        if(!(statement instanceof EnhancedMobileElement) || !(previousStatement instanceof EnhancedMobileElement)) {
            return false;
        }

        AppiumLocator target = ((EnhancedMobileElement) statement).getLocator();
        AppiumLocator src = ((EnhancedMobileElement) previousStatement).getLocator();
        if (target == null || src == null) {
            return false;
        }
        if (target.getStrategy().equalsIgnoreCase(src.getStrategy()) && target.getValue().equalsIgnoreCase(src.getValue())) {
            return true;
        }

        return false;
    }

    // 关键词序列引导搜索
    private MobileElement repairWithKSG(int keywordPos, EnhancedMobileElement statement, String oriLayoutXmlFile, List<EnhancedMobileElement> repairedPath, List<EnhancedTouchAction> repairedSwipe) {
        logger.info("Start to repair the mobile element...");

        MobileElement targetElement = null;
        Stack<StateVertix> exploreStack = new Stack<>();
        long exploreStartTime = System.currentTimeMillis();

        // 检查是否超时
        while(!isOutOfTime(exploreStartTime, exploreStack)) {
            if (!exploreStack.empty()) {
                // 捕获当前屏幕状态的层次布局文件
                curLayoutXmlFile = tempXmlSavedFolder + Settings.sep + System.currentTimeMillis() + Settings.XML_EXT;
                UtilsHierarchyXml.takeXmlSnapshot(driver, curLayoutXmlFile);
                // 探索栈中包含当前状态，表明探索遇到了回路
                if (exploreStack.contains(new StateVertix(curLayoutXmlFile))) {
                    logger.info("该状态已探索过，移除上一个点击事件并根据情况进行回退。。。");
                    // 此时应移除上一个点击事件
                    repairedPath.remove(repairedPath.size()-1);
                    if (!exploreStack.peek().equals(new StateVertix(curLayoutXmlFile))) {
                        // 探索栈栈顶状态与当前状态不一致，表明上一次点击带来了状态变换
                        // 此时应回退，以避免循环搜索
                        driver.navigate().back();
                    }
                    while (!exploreStack.empty() && exploreStack.peek().getElesToBeClicked().size() == 0) {
                        // 探索栈栈顶状态没有可点击元素时，表明该状态已经探索完毕
                        // 此时应当移除该状态，回退点击事件和应用状态
                        exploreStack.pop();
                        if (!exploreStack.empty()) {
                            repairedPath.remove(repairedPath.size()-1);
                            driver.navigate().back();
                        }
                    }
                    if (exploreStack.empty()) {
                        logger.info("元素未查找到。。。");
                        break;
                    } else {
                        // 拿出栈顶状态的可点击序列，点击第一个元素
                        fireEleAndAddToList(exploreStack.peek().getElesToBeClicked().remove(0), repairedPath);
                        continue;
                    }
                } else {
                    logger.info("探测到新状态，开始搜索。。。");
                }
            }

            // 在当前界面查找元素
            targetElement = UtilsRepair.searchForTargetElementOnState(driver, statement, oriLayoutXmlFile, repairedSwipe, isIdConsidered);
            if (targetElement != null) {
                if (repairedSwipe.isEmpty() && exploreStack.empty()) {
                    // 在当前页面直接修复，表明该情况属于元素修复
                    eleBrokenNum++;
                }
                break;
            }

            if (repairedPath.size() >= MAX_EXPLORE_DEPTH) {
                logger.info("探索超过 MAX_EXPLORE_DEPTH 个状态，开始回退。。。");
                repairedPath.remove(repairedPath.size()-1);
                driver.navigate().back();
                while (!exploreStack.empty() && exploreStack.peek().getElesToBeClicked().size() == 0) {
                    exploreStack.pop();
                    if (!exploreStack.empty()) {
                        repairedPath.remove(repairedPath.size()-1);
                        driver.navigate().back();
                    }
                }
                if (exploreStack.empty()) {
                    logger.info("元素未查找到。。。");
                    break;
                } else {
                    // 拿出栈顶状态的可点击序列，点击第一个元素
                    fireEleAndAddToList(exploreStack.peek().getElesToBeClicked().remove(0), repairedPath);
                    continue;
                }
            }

            // 在当前界面中没找到符合要求的修复元素，接下来根据关键词对该状态下的可点击元素进行语义排序
            // 决策出最相关的元素进行点击，到达下一个状态后查找目标元素
            // 如果当前页面状态不存在可点击元素，那么回退到上一个状态
            StateVertix curState = new StateVertix(curLayoutXmlFile);
            List<EnhancedMobileElement> clickableStmsOnState = UtilsRepair.fetchClickableStmsOnState(curLayoutXmlFile);
            if (clickableStmsOnState.isEmpty()) {
                // 当前页面状态不存在可点击元素
                // 若没有点击事件到达该状态，则表示此状态为搜索入口状态，此时已经没有可点击事件待搜索了，直接退出
                // 若通过上一个点击事件到达该状态，则回退到上一个状态，移除上一个点击事件
                if (repairedPath.size() == 0) {
                    logger.info("元素未查找到。。。");
                    break;
                }
                repairedPath.remove(repairedPath.size()-1);
                driver.navigate().back();
                while (!exploreStack.empty() && exploreStack.peek().getElesToBeClicked().size() == 0) {
                    exploreStack.pop();
                    if (!exploreStack.empty()) {
                        repairedPath.remove(repairedPath.size()-1);
                        driver.navigate().back();
                    }
                }
                if (exploreStack.empty()) {
                    logger.info("元素未查找到。。。");
                    break;
                } else {
                    // 拿出栈顶状态的可点击序列，点击第一个元素
                    fireEleAndAddToList(exploreStack.peek().getElesToBeClicked().remove(0), repairedPath);
                }
            } else {
                // 计算当前关键词与界面中的可点击元素的相似度得分
                Map<EnhancedMobileElement, Double> eleSimScoreMap = new HashMap<>();
                Iterator<EnhancedMobileElement> iterator = clickableStmsOnState.iterator();
                while(iterator.hasNext()) {
                    EnhancedMobileElement stm = iterator.next();
                    String temp = stm.getText() + " " + stm.getContentDesc();
                    if (temp.contains("More") || temp.contains("more")) {
                        // 考虑到路径增加的情况，很多功能被放置在 More options 按钮中，应将该关键词提高优先级
                        eleSimScoreMap.put(stm, 0.8);
                        continue;
                    }
                    if (temp.contains("Navigate up") || temp.contains("Navigate Up") ||
                            temp.contains("OK") || temp.contains("CANCEL")) {
                        // 考虑到 Navigate up 按钮将回退到上一页面状态，应取消该点击事件
                        // 考虑到 OK 和 CANCEL 按钮将回退到上一页面状态，应取消该点击事件
                        iterator.remove();
                        continue;
                    }
                    Set<String> stmTxtSet = new HashSet<>();
                    if (StringUtils.isNotBlank(temp)) stmTxtSet.addAll(WordsSplit.getWords(UtilsRepair.removeNewLines(temp.trim())));

                    double score = UtilsRepair.computeSimilarity(keywordList.get(keywordPos).getExtendSeq(), stmTxtSet);
                    eleSimScoreMap.put(stm, score);
                }

                // 根据相似度得分对界面中的可点击元素进行排序
                clickableStmsOnState.sort((o1, o2) -> {
                    // 按照分数从高到低排
                    if (eleSimScoreMap.get(o1) > eleSimScoreMap.get(o2)) {
                        return -1;
                    } else if (eleSimScoreMap.get(o1).equals(eleSimScoreMap.get(o2))) {
                        return 0;
                    }
                    return 1;
                });

                EnhancedMobileElement mostRelatedEle = clickableStmsOnState.remove(0);
                // 将剩余的排完序的可点击元素放入列表，将当前状态存入探索栈中，以便后续回退
                curState.setElesToBeClicked(clickableStmsOnState);
                exploreStack.push(curState);

                // 点击与当前关键词最相关的元素，并将其加入到点击序列中
                fireEleAndAddToList(mostRelatedEle, repairedPath);
            }

        }

        if (targetElement != null) {
            logger.info("目标元素已找到。。。");
        } else {
            logger.info("目标元素被移除。。。");
        }
        return targetElement;
    }

    // 检查是否超时
    private boolean isOutOfTime(long exploreStartTime, Stack exploreStack) {
        if (System.currentTimeMillis() - exploreStartTime > MAX_EXPLORE_TIME) {
            // 若探索时间超过规定的最长时间，则停止
            logger.info("探索超时。。。");
            while(!exploreStack.empty()){
                driver.navigate().back();
                exploreStack.pop();
            }
            return true;
        }
        return false;
    }

    // 根据测试语句获取点击元素，将该语句加入点击序列，并点击跳转到下个界面
    private void fireEleAndAddToList(EnhancedMobileElement clickableStm, List<EnhancedMobileElement> clickElementList) {
        AppiumLocator locator = clickableStm.getLocator();
        MobileElement elementToBeClicked = null;
        logger.info("Click Element: " + locator.toString());
        try {
            if (locator.getStrategy().equals("resourceId")) {
                elementToBeClicked = (MobileElement) driver.findElementById(locator.getValue());
            } else if (locator.getStrategy().equals("contentDesc")) {
                elementToBeClicked = (MobileElement) driver.findElementByAccessibilityId(locator.getValue());
            } else if (locator.getStrategy().equals("xpath")) {
                elementToBeClicked = (MobileElement) driver.findElementByXPath(locator.getValue());
            }
        } catch (NoSuchElementException e) {
            logger.info("界面变动，元素已不在当前界面，将点击下一个元素。。。");
            // 为了不影响整体流程，由于不会造成状态变换，因此会在下一个循环将该语句删除
            clickElementList.add(clickableStm);
            return;
        }

        clickElementList.add(clickableStm);
        elementToBeClicked.click();
        // 元素点击后需要等待 1s 以待新界面加载完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.info("Driver wait has been interrupted");
        }
    }

    // 根据修复语句 statement 决定对该元素的操作方式：clear、click、sendKeys
    private void fireEvent(MobileElement element, Statement statement){
        if (statement.getAction().equals(AppiumAction.Clear)) {
            logger.info("Clear Element: " + ((EnhancedMobileElement)statement).getLocator().toString());
            element.clear();
        } else if (statement.getAction().equals(AppiumAction.Click)) {
            logger.info("Click Element: " + ((EnhancedMobileElement)statement).getLocator().toString());
            element.click();
            // 元素点击后需要等待 1s 以待新界面加载完成
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info("Driver wait has been interrupted");
            }
        } else if (statement.getAction().equals(AppiumAction.SendKeys)) {
            logger.info("SendKeys Element: " + ((EnhancedMobileElement)statement).getLocator().toString());
            element.sendKeys(statement.getValue());
        } else {
            logger.info("UnKnown operation! Cannot fire event");
        }
    }

    // 根据崩溃测试用例和修复语句生成修复后的测试用例
    private EnhancedTestCase generateNewTest(EnhancedTestCase testBroken, Map<Integer, Statement> repairedStmMap, Map<Integer, List<EnhancedMobileElement>> repairedPathMap, Map<Integer, List<EnhancedTouchAction>> repairedSwipeMap) {
        // 打印失效测试用例
        System.out.println("\n Before repairing....");
        UtilsRepair.printTestCase(testBroken);

        // 示例：output/RepairedTC/LarkPlayer/TC1.java
        String repairedTCSavedPath = Settings.repairedTCPath + Settings.sep + appEnum.getAppName()
                + Settings.sep + testBroken.getName() + Settings.JAVA_EXT;
        EnhancedTestCase repairedTest = new EnhancedTestCase(testBroken.getName(), repairedTCSavedPath);
        int currentLine = 0;
        for (Integer i : repairedStmMap.keySet()) {
            if (currentLine == 0) currentLine = i;
            // 检查在该步骤前是否有新增步骤
            if (repairedPathMap.keySet().contains(i)) {
                List<EnhancedMobileElement> addElements = repairedPathMap.get(i);
                for (EnhancedMobileElement element : addElements) {
                    element.setLine(currentLine);
                    element.setAction(AppiumAction.Click);
                    element.setValue("");
                    repairedTest.addAndReplaceStatement(currentLine++, element);
                }
            }
            // 检查在该步骤前是否需要滑动界面
            if (repairedSwipeMap.keySet().contains(i)) {
                List<EnhancedTouchAction> swipeList = repairedSwipeMap.get(i);
                for (EnhancedTouchAction swipe : swipeList) {
                    swipe.setLine(currentLine);
                    repairedTest.addAndReplaceStatement(currentLine++, swipe);
                }
            }
            Statement statement = repairedStmMap.get(i);
            if (statement == null) continue;
            statement.setLine(currentLine);
            repairedTest.addAndReplaceStatement(currentLine++, statement);
        }

        // 打印修复完成测试用例
        System.out.println("\n After repairing....");
        UtilsRepair.printTestCase(repairedTest);
        System.out.println();
        return repairedTest;
    }

    // 处理退出信息
    private void shutDown(int pathAddNum, int swipeAddNum){
        // 打印修复信息
        System.out.println();
        logger.info("修复总共花费了 {} s。", String.format("%.3f", elapsedTime / 1000.0f));
        logger.info("共有 {} 条测试语句发生崩溃，其中元素修复 {} 条，路径增加 {} 条，滑动修复 {} 条，路径删除 {} 条。",
                brokenStmNum, eleRepairedNum, pathAddNum, swipeAddNum, deletedStmNum);
        logger.info("{}，{}；{}；{}，{}，{}；{}", eleRepairedNum, brokenStmNum - eleBrokenNum, eleRepairedNum, pathAddNum, swipeAddNum, deletedStmNum, String.format("%.1f", elapsedTime / 1000.0f));

        logger.info("正在处理退出信息。。。。。。");
        // driver.quit();

        // 清理临时层次布局文件存储目录
        File tempFolder = new File(tempXmlSavedFolder);
        String[] content = tempFolder.list();
        for(String name : content) {
            File temp = new File(tempXmlSavedFolder + Settings.sep + name);
            if(!temp.delete()){
                logger.error("Failed to delete " + name);
            }
        }
        logger.info("已成功退出。。。。。。。。。");
    }
}
