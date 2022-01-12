package test.java;

import main.java.util.UiNode;
import main.java.util.UtilsXmlLoader;
import main.java.util.UtilsXpath;
import main.java.util.XmlTreeNode;

import java.util.List;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/12 11:38
 */
public class XpathTest {
    public static void main(String[] args) {
        UtilsXmlLoader xmlLoader = new UtilsXmlLoader();
        xmlLoader.parseXml("D:\\DevelopmentTool\\IDEA\\MavenWorkspace\\Sigmoter\\output\\AppInfo\\APIDemo\\AppiumDemo\\41-hierarchy.xml");
        List<XmlTreeNode> nodeList = xmlLoader.getLeafNodes();
        UiNode curNode = new UiNode();

        for (XmlTreeNode node : nodeList) {
            if (((UiNode)node).getAttribute("text").equals("NFC")) {
                curNode = (UiNode) node;
            }
        }

        nodeList = xmlLoader.getAllNodes();

        String xpath1 = UtilsXpath.getIdentityXpath(nodeList, curNode);
        String xpath2 = UtilsXpath.getRelativeXpath(nodeList,curNode);
        String xpath3 = UtilsXpath.getFullXpath(curNode);

        if (xpath2.startsWith("//hierarchy")) {
            // 不存在可使用身份属性唯一定位的父节点，此时 xpath2 退化为 xpath3
            System.out.println(xpath1.equals("") ? xpath2 : (xpath1 + ";" + xpath2));
        } else {
            System.out.println(xpath1.equals("") ? (xpath2 + ";" + xpath3) : (xpath1 + ";" + xpath2 + ";" + xpath3));
        }

    }
}
