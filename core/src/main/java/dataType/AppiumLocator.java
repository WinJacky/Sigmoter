package main.java.dataType;

import java.io.Serializable;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/14 10:57
 */
public class AppiumLocator implements Serializable {
    String strategy, value;

    public AppiumLocator(String strategy, String value) {
        this.strategy = strategy;
        this.value = value;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "By." + strategy + "(\"" + value + "\")";
    }
}
