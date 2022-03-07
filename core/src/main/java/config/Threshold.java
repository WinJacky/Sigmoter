package main.java.config;

public enum Threshold {

    /**
     * 用于元素结构相似度判断，超过该值认为新旧元素相同
     */
    ELE_STRUCT_SIM(0.8),

    /**
     * 用于元素语义相似度判断，超过该值认为新旧元素相同
     */
    ELE_SEMAN_SIM(0.8),

    /**
     * 用于元素布局相似度判断，超过该值认为新旧元素相同
     */
    ELE_Layout_SIM(0.8),

    /**
     * 计算元素结构相似度时，xpath相似度所占比重
     */
    XPATH_WEIGHT(0.4),

    /**
     * 计算元素布局相似度时，元素中心距离相似度所占比重
     */
    DISTANCE_WEIGHT(0.3),

    /**
     * 计算元素综合相似度时，认定元素相似的阈值
     */
    ELE_SIM_SCORE(0.6),

    /**
     * 计算页面状态语义相似度时，认定页面相似的阈值
     */
    STATE_SIM_SCORE(0.9);

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