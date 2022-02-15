package main.java.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // 返回当前节点的兄弟节点，没有兄弟节点返回 null
    public List<UiNode> getBrotherNodes() {
        List<UiNode> broNodes = new ArrayList<>();
        for (XmlTreeNode node : getParent().getChildrenList()) {
            UiNode temp = (UiNode) node;
            if (!temp.equals(this)) broNodes.add(temp);
        }
        if (broNodes.size() == 0) return null;
        return broNodes;
    }

    @Override
    public String toString() {
        return "[class=\"" + getAttribute("class") + "\", text=\"" + getAttribute("text") + "\", bounds=\"" + getAttribute("bounds") + "\"]";
    }
}
