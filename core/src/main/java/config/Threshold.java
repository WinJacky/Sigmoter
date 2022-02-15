package main.java.config;

public enum Threshold {

    /**
     * 用于元素结构相似度判断，超过该值认为新旧元素相同
     */
    ELE_STRUCT_SIM(0.8),

    /**
     * 用于元素语义相似度判断，超过该值认为新旧元素相同
     */
    ELE_SEMAN_SIM(0.7),

    /**
     * 用于元素布局相似度判断，超过该值认为新旧元素相同
     */
    ELE_Layout_SIM(0.7);

    private double value;

    Threshold(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}