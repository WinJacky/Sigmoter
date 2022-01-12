package test.java;

import main.java.util.DebugBridge;
import main.java.util.UtilsHierarchyXml;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/4 15:52
 */
public class HierarchyXmlTest {
    public static void main(String[] args) {
        DebugBridge.init();

        // 截取当前Android模拟器上的层次布局，并将其保存到指定本地路径
        UtilsHierarchyXml.getHierarchyXmlFile("C:\\Users\\Feisher\\Desktop\\temp.xml");

        DebugBridge.terminate();
    }
}
