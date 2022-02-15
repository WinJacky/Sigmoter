package main.java.util;

import com.google.gson.Gson;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import main.java.config.Threshold;
import main.java.dataType.*;
import main.java.runner.RepairRunner;
import main.java.utils.WordsSplit;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilsRepair {
    private static Logger log = LoggerFactory.getLogger(UtilsRepair.class);

    // 深拷贝
    public static Statement deepClone(Statement object) {
        Gson gson = new Gson();
        if (object instanceof EnhancedMobileElement) {
            return gson.fromJson(gson.toJson(object), EnhancedMobileElement.class);
        } else if (object instanceof EnhancedTouchAction) {
            return gson.fromJson(gson.toJson(object), EnhancedTouchAction.class);
        }else if (object instanceof EnhancedNavigate) {
            return gson.fromJson(gson.toJson(object), EnhancedNavigate.class);
        }
        log.error("Object is not Statement type...");
        return null;
    }

    public static MobileElement retrieveElementFromAppiumLocator(AndroidDriver driver, AppiumLocator locator) {
        String strategy = locator.getStrategy();
        String value = locator.getValue();
        List element = null;

        if ("resourceId".equalsIgnoreCase(strategy)) {
            element = driver.findElementsById(value);
        } else if ("contentDesc".equalsIgnoreCase(strategy)) {
            element = driver.findElementsByAccessibilityId(value);
        } else if ("xpath".equalsIgnoreCase(strategy)) {
            element = driver.findElementsByXPath(value);
        }

        if (element.size() > 1) {
            throw new RuntimeException("Using original locator cannot uniquely locate elemet!");
        } else if (element.size() == 0) {
            throw new NoSuchElementException("Using original locator cannot find element!");
        }
        return (MobileElement) element.get(0);
    }

    // 计算根据旧元素定位信息查找到的新元素，与旧元素之间的结构相似度得分
    public static double checkElementByCollectedInfo(String curLayoutXmlFile, MobileElement element, EnhancedMobileElement collectedInfo) {
        if (StringUtils.equalsIgnoreCase(element.getAttribute("className"), collectedInfo.getClassName())) {
            // 对 element 和 collectedInfo 的绝对Xpath路径进行比较
            String xpath1 = UtilsXpath.getElementAbsoluteXpath(element, curLayoutXmlFile);
            String xpath2 = collectedInfo.getXpath();
            xpath2 = xpath2.substring(xpath2.indexOf("//hierarchy"));

            double pho1 = UtilsSimilarity.simOfXpath(xpath1, xpath2);
            double pho2 = 0.0;
            double divid = 17.0;
            String temp;
            // 身份属性
            temp = element.getAttribute("resourceId");
            if (temp == null && collectedInfo.getResourceId() == null) divid-=3;
            else if (temp != null && temp.equalsIgnoreCase(collectedInfo.getResourceId())) pho2+=3;
            temp = element.getAttribute("contentDescription");
            if (temp == null && collectedInfo.getContentDesc() == null) divid-=3;
            else if (temp != null && temp.equalsIgnoreCase(collectedInfo.getContentDesc())) pho2+=3;
            temp = element.getAttribute("text");
            if (temp == null && collectedInfo.getText() == null) divid-=3;
            else if (temp != null && temp.equalsIgnoreCase(collectedInfo.getText())) pho2+=3;

            // 重要属性
            if (element.getAttribute("checkable").equals(Boolean.toString(collectedInfo.isCheckable()))) pho2+=2;
            if (element.getAttribute("clickable").equals(Boolean.toString(collectedInfo.isClickable()))) pho2+=2;
            if (element.getAttribute("scrollable").equals(Boolean.toString(collectedInfo.isScrollable()))) pho2+=2;

            // 非重要属性
            if (element.getAttribute("focusable").equals(Boolean.toString(collectedInfo.isFocusable()))) pho2++;
            if (element.getAttribute("longClickable").equals(Boolean.toString(collectedInfo.isLongClickable()))) pho2++;

            pho2 = pho2 / divid;
            double alpha = 0.4;
            return (pho1 * alpha + (pho2) * (1 - alpha));
        }
        return 0.0;
    }

    // 计算根据旧元素定位信息查找到的新元素，与旧元素之间的语义相似度得分
    public static double checkElementBySemantic(MobileElement element, EnhancedMobileElement statement) {
        // 提取元素关键信息
        // 此处元素关键信息和关键词相比较
        Set<String> set = new HashSet<>();
        String temp = element.getAttribute("resourceId");
        if (StringUtils.isNotBlank(temp)) {
            if (temp.contains(":id/")) {
                temp = temp.substring(temp.indexOf("/") + 1);
            }
            set.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
        }
        temp = element.getAttribute("contentDescription");
        if (StringUtils.isNotBlank(temp)) set.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
        temp = element.getAttribute("text");
        if (StringUtils.isNotBlank(temp)) set.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));

        Set<String> set1 = new HashSet<>();
        temp = statement.getResourceId();
        if (StringUtils.isNotBlank(temp)) {
            if (temp.contains(":id/")) {
                temp = temp.substring(temp.indexOf("/") + 1);
            }
            set1.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
        }
        temp = statement.getContentDesc();
        if (StringUtils.isNotBlank(temp)) set1.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
        temp = statement.getText();
        if (StringUtils.isNotBlank(temp)) set1.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));

        return computeSimilarity(set,set1);
    }

    // 将字符串中的空白字符替换为空格
    public static String removeNewLines(String str) {
        return str.replaceAll("[\\t\\n\\f\\r]", " ");
    }

    // 计算两个文本集合之间的语义相似度
    private static double computeSimilarity(Set<String> set1, Set<String> set2) {
        double sumScore1 = 0.0;
        for (String s1: set1) {
            double maxScore = 0.0;
            for (String s2: set2) {
                maxScore = Math.max(maxScore, RepairRunner.word2Vec.getSimWith2Words(s1, s2));
            }
            sumScore1 += maxScore;
        }
        sumScore1 =  sumScore1 / set1.size();

        double sumScore2 = 0.0;
        for (String s1: set2) {
            double maxScore = 0.0;
            for (String s2: set1) {
                maxScore = Math.max(maxScore, RepairRunner.word2Vec.getSimWith2Words(s1, s2));
            }
            sumScore2 += maxScore;
        }
        sumScore2 =  sumScore2 / set2.size();

        return Math.max(sumScore1, sumScore2);
    }

    // 计算根据旧元素定位信息查找到的新元素，与旧元素之间的布局相似度得分
    public static double checkElementByLayout(MobileElement element, EnhancedMobileElement statement, Dimension windowSize, String oriLayoutXmlFile, String curLayoutXmlFile) {
        // 计算新旧元素中心坐标之间的距离得分
        Point curElePoint = element.getCenter();
        Point location = statement.getCoordinate();
        Dimension dimension = statement.getDimension();
        Point oldElePoint = new Point(location.getX()+dimension.width/2, location.getY()+dimension.height/2);

        double centerDis = getEuclideanDistance(curElePoint, oldElePoint);
        double maxDis = getMaxBoundDistance(oldElePoint, windowSize);
        double disScore = 1 - centerDis / maxDis;

        // disScore 所占比重
        double alpha = 0.3;
        // 计算兄弟节点匹配成功的数目
        UtilsXmlLoader xmlLoader = new UtilsXmlLoader();
        xmlLoader.parseXml(oriLayoutXmlFile);
        List<XmlTreeNode> nodeList = xmlLoader.getLeafNodes();
        // 在解析得到的所有叶节点中找到给定的 UI 元素
        UiNode originNode = UtilsXpath.getNodeByStatement(statement, nodeList);
        if(originNode == null) return disScore;

        List<UiNode> oriBroNodes = originNode.getBrotherNodes();
        if(oriBroNodes == null) return disScore;
        int oriBroNum = oriBroNodes.size(), matchedBroNum = 0;
        List<Set<String>> broTextList = new ArrayList<>();
        for (UiNode node : oriBroNodes) {
            Set<String> broText = new HashSet<>();
            String temp = node.getAttribute("resource-id");
            if (StringUtils.isNotBlank(temp)) {
                if (temp.contains(":id/")) {
                    temp = temp.substring(temp.indexOf("/") + 1);
                }
                broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
            }
            temp = node.getAttribute("content-desc");
            if (StringUtils.isNotBlank(temp)) broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
            temp = node.getAttribute("text");
            if (StringUtils.isNotBlank(temp)) broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));

            // 三个身份属性都为空，则认为该兄弟节点没有匹配价值
            if (broText.isEmpty()) {
                oriBroNum--;
            } else {
                broTextList.add(broText);
            }
        }
        if(oriBroNum == 0) return disScore;

        xmlLoader.parseXml(curLayoutXmlFile);
        nodeList = xmlLoader.getLeafNodes();
        UiNode curNode = UtilsXpath.getNodeByElement(element, nodeList);
        if(curNode == null) return disScore;

        List<UiNode> curBroNodes = curNode.getBrotherNodes();
        if(curBroNodes == null) return disScore * alpha;
        int curBroNum = curBroNodes.size();
        List<Set<String>> curBroTextList = new ArrayList<>();
        for (UiNode node : curBroNodes) {
            Set<String> broText = new HashSet<>();
            String temp = node.getAttribute("resource-id");
            if (StringUtils.isNotBlank(temp)) {
                if (temp.contains(":id/")) {
                    temp = temp.substring(temp.indexOf("/") + 1);
                }
                broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
            }
            temp = node.getAttribute("content-desc");
            if (StringUtils.isNotBlank(temp)) broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));
            temp = node.getAttribute("text");
            if (StringUtils.isNotBlank(temp)) broText.addAll(WordsSplit.getWords(removeNewLines(temp.trim())));

            if (broText.isEmpty()) {
                curBroNum--;
            } else {
                curBroTextList.add(broText);
            }
        }

        if (curBroNum == 0) {
            return disScore * alpha;
        } else {
            for (Set<String> broText : broTextList) {
                for (Set<String> curBroText : curBroTextList) {
                    // 如果两个文本集合语义相似度达到 ELE_SEMAN_SIM，则认为新旧兄弟节点匹配成功
                    if (computeSimilarity(broText, curBroText) >= Threshold.ELE_SEMAN_SIM.getValue()) {
                        matchedBroNum++;
                        curBroTextList.remove(curBroText);
                        break;
                    }
                }
            }

            return disScore * alpha + (matchedBroNum / oriBroNum) * (1 - alpha);
        }
    }

    // 计算两个点的欧氏距离
    public static double getEuclideanDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
    }

    // 计算坐到达屏幕边界最远的距离
    public static double getMaxBoundDistance(Point point,Dimension windowSize) {
        double max1 =  Math.max(getEuclideanDistance(new Point(0,0), point),
                getEuclideanDistance(new Point(windowSize.width, 0), point));
        double max2 = Math.max(getEuclideanDistance(new Point(0, windowSize.height), point),
                getEuclideanDistance(new Point(windowSize.width, windowSize.height), point));
        return Math.max(max1, max2);
    }

    // 在当前页面状态中搜索目标元素
    public static MobileElement searchForTargetElementOnState(AndroidDriver driver, EnhancedMobileElement statement, String oriLayoutXmlFile, String curLayoutXmlFile) {
        // 根据元素属性查找
        Set<MobileElement> result = new HashSet<>();
        List<MobileElement> tempResult;
        // 根据元素 classname 查找
        tempResult = driver.findElementsByClassName(statement.getClassName());
        if (tempResult != null) result.addAll(tempResult);
        // 根据身份属性查找
        if (StringUtils.isNotEmpty(statement.getResourceId())) {
            tempResult = driver.findElementsById(statement.getResourceId());
            if (tempResult != null) result.addAll(tempResult);
        }
        if (StringUtils.isNotEmpty(statement.getContentDesc())) {
            tempResult = driver.findElementsByAccessibilityId(statement.getContentDesc());
            if (tempResult != null) result.addAll(tempResult);
        }
        if (StringUtils.isNotEmpty(statement.getText())) {
            tempResult = driver.findElementsByXPath("//" + statement.getClassName() + "[@text='" + statement.getText() + "']");
            if (tempResult != null) result.addAll(tempResult);
        }
        // 根据 xpath 路径查找
        String[] xpathArray = statement.getXpath().split(";");
        for (String xpath : xpathArray) {
            tempResult = driver.findElementsByXPath(xpath);
            if (tempResult != null) result.addAll(tempResult);
        }

        // 检查相似度
        double maxSimScore = 0.0;
        MobileElement mostSimElement = null;
        for (MobileElement ele : result) {
            if (!ele.isDisplayed()) continue;
            // 结构相似度
            double sim1 = checkElementByCollectedInfo(curLayoutXmlFile, ele, statement);
            // 语义相似度
            double sim2 = checkElementBySemantic(ele, statement);
            // 布局相似度
            double sim3 = checkElementByLayout(ele, statement, driver.manage().window().getSize(), oriLayoutXmlFile, curLayoutXmlFile);
            double beta = 0.3;
            double score = sim1 * beta + sim2 * beta + sim3 * (1 - 2 * beta);

            if (score >= 0.5 && score > maxSimScore) {
                maxSimScore = score;
                mostSimElement = ele;
            }
        }

        return mostSimElement;
    }

    // 对于查找到的修复元素，根据原测试语句的定位策略决策出合适的定位器
    public static AppiumLocator getAppropriateLocator(AndroidDriver driver, EnhancedMobileElement statement, MobileElement candidateElement, String curLayoutXmlFile) {
        AppiumLocator originLocator = statement.getLocator();
        if (originLocator.getStrategy().equals("resourceId")) {
            if (driver.findElementsById(candidateElement.getAttribute("resourceId")).size() == 1) {
                return new AppiumLocator("resourceId", candidateElement.getAttribute("resourceId"));
            }
        } else if (originLocator.getStrategy().equals("contentDesc")) {
            if (driver.findElementsByAccessibilityId(candidateElement.getAttribute("contentDescription")).size() == 1) {
                return new AppiumLocator("contentDesc", candidateElement.getAttribute("contentDescription"));
            }
        }
        String xpath = UtilsXpath.getElementOptimalXpath(candidateElement,curLayoutXmlFile);
        return new AppiumLocator("xpath", xpath);
    }

    public static void printTestCase(EnhancedTestCase tc) {
        for (Integer i: tc.getStatements().keySet()) {
            System.out.println(tc.getStatements().get(i).getLine() + ":\t" + tc.getStatements().get(i) + ";");
        }
    }

    public static void saveTest(ParseTest pt, EnhancedTestCase testRepaired, String oldPath) {
        try {
            pt.parseAndSaveToJava(testRepaired, oldPath);
        } catch (IOException e) {
            log.error("An error occurred when saving the repaired test into java file...");
            e.printStackTrace();
        }
    }
}
