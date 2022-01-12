package main.java.util;

import java.util.LinkedHashMap;
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

}
