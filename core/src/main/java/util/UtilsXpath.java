package main.java.util;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/7/5 11:20
 */

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import main.java.config.Settings;
import main.java.dataType.AppiumLocator;
import main.java.dataType.EnhancedMobileElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class UtilsXpath {

    private static Logger logger = LoggerFactory.getLogger(UtilsXpath.class);

    /**
     * 给定待定位元素和层次布局文件，生成其多个 Xpath 路径
     * 分别使用以下定位策略：
     *     1. 使用其自身的身份属性信息定位
     *     2. 使用距离其最近的可唯一定位的父节点相对定位
     *     3. 使用全Xpath路径定位
     */
    public static String getElementHybridXPath(MobileElement element, String hierarchyLayoutXmlFile) {
        UtilsXmlLoader xmlLoader = new UtilsXmlLoader();
        xmlLoader.parseXml(hierarchyLayoutXmlFile);
        List<XmlTreeNode> nodeList = xmlLoader.getLeafNodes();

        // 在解析得到的所有叶节点中找到给定的 UI 元素
        UiNode currentNode = getNodeByEleOrSta(element, nodeList);
        if(currentNode == null) {
            return "";
        }

        nodeList = xmlLoader.getAllNodes();

        String xpath1 = getIdentityXpath(nodeList, currentNode);
        String xpath2 = getRelativeXpath(nodeList,currentNode);
        String xpath3 = getFullXpath(currentNode);

        if (xpath2.startsWith("//hierarchy")) {
            // 不存在可使用身份属性唯一定位的父节点，此时 xpath2 退化为 xpath3
            return xpath1.equals("") ? xpath2 : (xpath1 + ";" + xpath2);
        }

        return xpath1.equals("") ? (xpath2 + ";" + xpath3) : (xpath1 + ";" + xpath2 + ";" + xpath3);
    }

    /**
     * 给定待定位元素和层次布局文件，生成其最优 Xpath 路径
     */
    public static String getElementOptimalXpath(AndroidDriver driver, MobileElement element, String hierarchyLayoutXmlFile) {
        UtilsXmlLoader xmlLoader = new UtilsXmlLoader();
        xmlLoader.parseXml(hierarchyLayoutXmlFile);
        List<XmlTreeNode> nodeList = xmlLoader.getLeafNodes();

        // 在解析得到的所有叶节点中找到给定的 UI 元素
        UiNode currentNode = getNodeByEleOrSta(element, nodeList);
        if (currentNode == null) {
            hierarchyLayoutXmlFile = Settings.TEMP_XML_SAVED_FOLDER + Settings.sep + System.currentTimeMillis() + Settings.XML_EXT;
            UtilsHierarchyXml.takeXmlSnapshot(driver, hierarchyLayoutXmlFile);
            xmlLoader.parseXml(hierarchyLayoutXmlFile);
            nodeList = xmlLoader.getLeafNodes();
            currentNode = getNodeByEleOrSta(element, nodeList);
        }

        nodeList = xmlLoader.getAllNodes();

        // 尝试使用该节点的身份属性信息定位该节点
        String xpath = getIdentityLocator(nodeList, currentNode, "");

        while(!xpath.startsWith("//")) {
            // 身份属性定位失败（该元素的三个身份属性很可能都为空），递归寻找其可使用身份属性唯一定位的父节点，再使用相对位置信息定位该元素
            if (currentNode.getParent() instanceof RootWindowNode) {
                // 已上溯到根节点还未找到可使用身份属性唯一定位的父节点，此时使用全Xpath路径定位元素
                xpath = "//hierarchy" + xpath;
                break;
            }

            currentNode = (UiNode) currentNode.getParent();
            xpath = getIdentityLocator(nodeList, currentNode, xpath);
        }

        return xpath;
    }

    /**
     * 给定待定位元素和层次布局文件，生成其全Xpath路径定位
     */
    public static String getElementAbsoluteXpath(MobileElement element, String hierarchyLayoutXmlFile) {
        UtilsXmlLoader xmlLoader = new UtilsXmlLoader();
        xmlLoader.parseXml(hierarchyLayoutXmlFile);
        List<XmlTreeNode> nodeList = xmlLoader.getLeafNodes();

        // 在解析得到的所有叶节点中找到给定的 UI 元素
        UiNode currentNode = getNodeByEleOrSta(element, nodeList);
        if(currentNode == null) {
            return "";
        }

        return getFullXpath(currentNode);
    }

    /**
     * @param element 获取给定元素或测试语句对应元素的 bound 属性，通过元素位置信息唯一定位 UiNode
     * @param nodeList 解析得到的所有叶节点
     * @return 与给定 UI 元素对应的 UiNode，没找到返回 null
     */
    public static UiNode getNodeByEleOrSta(Object element, List<XmlTreeNode> nodeList) {
        Rectangle rect = null;
        if (element instanceof MobileElement) {
            rect = ((MobileElement) element).getRect();
        } else if (element instanceof EnhancedMobileElement) {
            rect = new Rectangle(((EnhancedMobileElement)element).getCoordinate(), ((EnhancedMobileElement)element).getDimension());
        }

        for (XmlTreeNode node : nodeList) {
            String[] boundStr = ((UiNode)node).getAttribute("bounds").substring(1).split("[,\\[\\]]+");
            int[] bounds = Arrays.stream(boundStr).mapToInt(Integer::parseInt).toArray();
            if(bounds[0] == rect.x && bounds[1] == rect.y) {
                int width = bounds[2] - bounds[0];
                int height = bounds[3] - bounds[1];
                if(rect.width == width && rect.height == height) {
                    return (UiNode) node;
                }
            }
        }

        if (element instanceof MobileElement) {
            MobileElement temp = (MobileElement) element;
            logger.info(temp.getText() + " " + temp.getLocation() + " " + temp.getSize());
            logger.info("页面加载导致布局文件过期，正在重新获取界面。。。");
        }
        return null;
    }

    /**
     * 对于给定的属性键值对，判断其在整个层次布局中是否可以唯一定位某一元素
     * @param nodeList XML 文件解析出的所有节点
     * @param pair 身份属性键值对，有效的仅包括 text、resource-id、content-desc
     * @return 可唯一定位返回 True，否则返回 False
     */
    public static Boolean isUnique(List<XmlTreeNode> nodeList, AttributePair... pair) {
        int count = 0;

        for (XmlTreeNode node : nodeList) {
            boolean flag = true;
            for(AttributePair attr : pair) {
                if(node instanceof RootWindowNode) {
                    flag = false;
                    break;
                }
                if(!attr.getValue().equals(((UiNode)node).getAttribute(attr.getKey()))) {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                count++;
                if(count >= 2) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 根据当前的节点获取其身份属性定位器
     * @param nodeList XML 文件解析出的所有节点，用以判断当前节点的身份属性是否可以唯一定位
     * @param currentNode 当前节点，可能是最终定位节点，也可能是其父节点
     * @param xpath 已经生成的 xpath 序列，加在 currentNode 定位信息后面
     * @return 当前节点可使用身份属性唯一定位，则返回最终结果（例：//class1[@text="SEU"]/class2[2]）
     *         当前节点无法使用身份属性唯一定位，则返回相对结果，以期进一步递归查找父节点（例：/class1[3]/class2[2]）
     */
    public static String getIdentityLocator(List<XmlTreeNode> nodeList, UiNode currentNode, String xpath) {
        String className = currentNode.getAttribute("class");
        String content_desc = currentNode.getAttribute("content-desc");
        String resource_id = currentNode.getAttribute("resource-id");
        String text = currentNode.getAttribute("text");
        int len = xpath.length();

        if (StringUtils.isNotBlank(content_desc) && isUnique(nodeList, new AttributePair("content-desc", content_desc))){
            xpath = "//" + className + "[@content-desc='" + content_desc + "']" + xpath;
        } else {
            if (StringUtils.isNotBlank(resource_id) && isUnique(nodeList, new AttributePair("resource-id", resource_id))) {
                xpath = "//" + className + "[@resource-id='" + resource_id + "']" + xpath;
            } else {
                if (StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("text", text))) {
                    xpath = "//" + className + "[@text=\"" + text + "\"]" + xpath;
                }
            }
        }

        if(xpath.length() != len) {
            return xpath;
        }

        // 单一身份属性（text、resource-id、content-desc）无法唯一定位元素，尝试多个身份属性联合定位
        if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(resource_id) && isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("resource-id", resource_id))){
            xpath = "//" + className + "[@content-desc='" + content_desc + "' and @resource-id='" + resource_id + "']" + xpath;
        } else {
            if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("text", text))){
                xpath = "//" + className + "[@content-desc='" + content_desc + "' and @text=\"" + text + "\"]" + xpath;
            } else {
                if (StringUtils.isNotBlank(resource_id) && StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("resource-id", resource_id), new AttributePair("text", text))){
                    xpath = "//" + className + "[@resource-id='" + resource_id + "' and @text=\"" + text + "\"]" + xpath;
                } else {
                    if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(resource_id) && StringUtils.isNotBlank(text) &&
                            isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("resource-id", resource_id), new AttributePair("text", text))){
                        xpath = "//" + className + "[@content-desc='" + content_desc + "' and @resource-id='" + resource_id + "' and @text=\"" + text + "\"]" + xpath;
                    }
                }
            }
        }

        if (xpath.length() != len) {
            return xpath;
        } else {
            return "/" + className + "[" + currentNode.classNameIndex + "]" + xpath;
        }
    }

    /**
     * 使用自身的身份属性信息定位
     * 尽量使用一种属性定位，其次使用两种，最次使用三种
     * 使用一种或两种身份属性可以唯一定位时，若多个皆可唯一定位，统统加进来
     *      例：xpath = //class[@content-desc='SEU'];//class[@text="SEU"]
     */
    public static String getIdentityXpath(List<XmlTreeNode> nodeList, UiNode currentNode) {
        String className = currentNode.getAttribute("class");
        String content_desc = currentNode.getAttribute("content-desc");
        String resource_id = currentNode.getAttribute("resource-id");
        String text = currentNode.getAttribute("text");
        String xpath = "";

        if (StringUtils.isNotBlank(content_desc) && isUnique(nodeList, new AttributePair("content-desc", content_desc))){
            xpath += "//" + className + "[@content-desc='" + content_desc + "'];";
        }
        if (StringUtils.isNotBlank(resource_id) && isUnique(nodeList, new AttributePair("resource-id", resource_id))) {
            xpath += "//" + className + "[@resource-id='" + resource_id + "'];";
        }
        if (StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("text", text))) {
            xpath += "//" + className + "[@text=\"" + text + "\"];";
        }

        if(!xpath.equals("")) {
            return xpath.substring(0, xpath.length()-1);
        }

        // 单一身份属性（text、resource-id、content-desc）无法唯一定位元素，尝试两个身份属性联合定位
        if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(resource_id) && isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("resource-id", resource_id))){
            xpath += "//" + className + "[@content-desc='" + content_desc + "' and @resource-id='" + resource_id + "'];";
        }
        if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("text", text))){
            xpath += "//" + className + "[@content-desc='" + content_desc + "' and @text=\"" + text + "\"];";
        }
        if (StringUtils.isNotBlank(resource_id) && StringUtils.isNotBlank(text) && isUnique(nodeList, new AttributePair("resource-id", resource_id), new AttributePair("text", text))){
            xpath += "//" + className + "[@resource-id='" + resource_id + "' and @text=\"" + text + "\"];";
        }

        if(!xpath.equals("")) {
            return xpath.substring(0, xpath.length()-1);
        }

        // 双身份属性无法唯一定位元素，尝试三个身份属性联合定位
        if (StringUtils.isNotBlank(content_desc) && StringUtils.isNotBlank(resource_id) && StringUtils.isNotBlank(text) &&
                isUnique(nodeList, new AttributePair("content-desc", content_desc), new AttributePair("resource-id", resource_id), new AttributePair("text", text))){
            xpath = "//" + className + "[@content-desc='" + content_desc + "' and @resource-id='" + resource_id + "' and @text=\"" + text + "\"]";
        }

        return xpath;
    }

    /**
     * 对于给定的节点，赋予其最优的定位策略，使其可以通过 driver.findElement 在界面中被找到
     */
    public static EnhancedMobileElement castNode2Element(List<XmlTreeNode> nodeList, UiNode currentNode) {
        EnhancedMobileElement element = new EnhancedMobileElement();
        element.setContentDesc(currentNode.getAttribute("content-desc"));
        element.setText(currentNode.getAttribute("text"));

        AppiumLocator locator;
        String resource_id = currentNode.getAttribute("resource-id");
        String content_desc = currentNode.getAttribute("content-desc");

        if (StringUtils.isNotBlank(resource_id) && isUnique(nodeList, new AttributePair("resource-id", resource_id))) {
            locator = new AppiumLocator("resourceId", resource_id);
            element.setLocator(locator);
            return element;
        }
        if (StringUtils.isNotBlank(content_desc) && isUnique(nodeList, new AttributePair("content-desc", content_desc))){
            locator = new AppiumLocator("contentDesc", content_desc);
            element.setLocator(locator);
            return element;
        }

        // 尝试使用该节点的身份属性信息定位该节点
        String xpath = getIdentityLocator(nodeList, currentNode, "");

        while(!xpath.startsWith("//")) {
            // 身份属性定位失败（该元素的三个身份属性很可能都为空），递归寻找其可使用身份属性唯一定位的父节点，再使用相对位置信息定位该元素
            if (currentNode.getParent() instanceof RootWindowNode) {
                // 已上溯到根节点还未找到可使用身份属性唯一定位的父节点，此时使用全Xpath路径定位元素
                xpath = "//hierarchy" + xpath;
                break;
            }

            currentNode = (UiNode) currentNode.getParent();
            xpath = getIdentityLocator(nodeList, currentNode, xpath);
        }

        locator = new AppiumLocator("xpath", xpath);
        element.setLocator(locator);
        return element;
    }

    /**
     * 根据可使用身份属性信息唯一定位的父节点，获取相对定位
     */
    public static String getRelativeXpath(List<XmlTreeNode> nodeList, UiNode currentNode) {
        String xpath = "/" + currentNode.getAttribute("class") + "[" + currentNode.classNameIndex + "]";

        while(!xpath.startsWith("//")) {
            // 寻找其可使用身份属性唯一定位的父节点，再使用相对位置信息定位该元素
            if (currentNode.getParent() instanceof RootWindowNode) {
                // 已上溯到根节点还未找到可使用身份属性唯一定位的父节点，此时使用全Xpath路径定位元素
                xpath = "//hierarchy" + xpath;
                break;
            }
            currentNode = (UiNode) currentNode.getParent();
            xpath = getIdentityLocator(nodeList, currentNode, xpath);
        }
        return xpath;
    }

    /**
     * 获取当前节点的全Xpath路径
     */
    public static String getFullXpath(UiNode currentNode) {
        StringBuilder xpath = new StringBuilder();

        while(true) {
            xpath.insert(0, "/" + currentNode.getAttribute("class") + "[" + currentNode.classNameIndex + "]");
            if (currentNode.getParent() instanceof RootWindowNode) {
                xpath.insert(0, "//hierarchy");
                break;
            }
            currentNode = (UiNode) currentNode.getParent();
        }
        return xpath.toString();
    }
}

/**
 *  元素属性键值对
 */
class AttributePair {
    private String key;
    private String value;

    public AttributePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}