package main.java.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;

import java.util.*;
import java.util.List;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/5 16:27
 */
public class UiNode extends XmlTreeNode {
    // use LinkedHashMap to preserve the order of the attributes
    private final Map<String, String> mAttributes = new LinkedHashMap<>();

    public void addAtrribute(String key, String value) {
        mAttributes.put(key, value);
    }

    public String getAttribute(String key) {
        return mAttributes.get(key);
    }

    // 获取当前元素右下角顶点的 y 坐标
    public int getY2() {
        if (StringUtils.isNotBlank(getAttribute("bounds"))) {
            String[] boundStr = getAttribute("bounds").substring(1).split("[,\\[\\]]+");
            int[] bounds = Arrays.stream(boundStr).mapToInt(Integer::parseInt).toArray();
            return bounds[3];
        }
        return 0;
    }

    // 获取当前元素的尺寸
    public Dimension getDimension() {
        if (StringUtils.isNotBlank(getAttribute("bounds"))) {
            String[] boundStr = getAttribute("bounds").substring(1).split("[,\\[\\]]+");
            int[] bounds = Arrays.stream(boundStr).mapToInt(Integer::parseInt).toArray();
            int width = bounds[2] - bounds[0];
            int height = bounds[3] - bounds[1];
            return new Dimension(width, height);
        }
        return null;
    }

    // 获取当前节点的兄弟节点，没有兄弟节点返回空 List
    public List<UiNode> getBrotherNodes() {
        List<UiNode> broNodes = new ArrayList<>();
        for (XmlTreeNode node : getParent().getChildrenList()) {
            UiNode temp = (UiNode) node;
            if (!temp.equals(this)) broNodes.add(temp);
        }
        return broNodes;
    }

    // 获取当前节点下属的所有叶子节点
    public List<UiNode> getLeafNodes() {
        List<UiNode> leaves = new ArrayList<>();
        if (!hasChild()) {
            leaves.add(this);
            return leaves;
        }

        for (XmlTreeNode child : mChildren) {
            leaves.addAll(((UiNode) child).getLeafNodes());
        }
        return leaves;
    }

    @Override
    public String toString() {
        return "[class=\"" + getAttribute("class") + "\", text=\"" + getAttribute("text") + "\", bounds=\"" + getAttribute("bounds") + "\"]";
    }
}
