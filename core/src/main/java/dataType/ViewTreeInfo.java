package main.java.dataType;

import io.appium.java_client.MobileElement;
import main.java.util.UtilsXpath;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 14:51
 */
public class ViewTreeInfo {

    private String xpath = "";
    private String className = "";
    private String resourceId = "";
    private String contentDesc = "";
    private String text = "";

    private boolean checkable;
    private boolean clickable;
    private boolean scrollable;
    private boolean focusable;
    private boolean longClickable;


    // 目标元素左上角的横纵坐标
    private int x;
    private int y;

    // 目标元素宽高
    private int width;
    private int height;

    public ViewTreeInfo(MobileElement me, String hierarchyLayoutXmlFile) {
        if (me != null) {
            this.xpath = UtilsXpath.getElementHybridXPath(me, hierarchyLayoutXmlFile);
            // 如：android.widget.TextView
            this.className = me.getAttribute("className");
            this.resourceId = me.getAttribute("resourceId");
            // 获取content-desc属性值，该属性使用AccessibilityId定位
            this.contentDesc = me.getAttribute("contentDescription");
            this.text = me.getAttribute("text");

            this.checkable = Boolean.parseBoolean(me.getAttribute("checkable"));
            this.clickable = Boolean.parseBoolean(me.getAttribute("clickable"));
            this.scrollable = Boolean.parseBoolean(me.getAttribute("scrollable"));
            this.focusable = Boolean.parseBoolean(me.getAttribute("focusable"));
            this.longClickable = Boolean.parseBoolean(me.getAttribute("longClickable"));

            this.x = me.getLocation().getX();
            this.y = me.getLocation().getY();

            this.width = me.getSize().getWidth();
            this.height = me.getSize().getHeight();
        }
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public void setContentDesc(String contentDesc) {
        this.contentDesc = contentDesc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isLongClickable() {
        return longClickable;
    }

    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        ViewTreeInfo other = (ViewTreeInfo) obj;
        if(xpath == null) {
            return other.xpath == null;
        } else {
            return xpath.equals(other.xpath);
        }
    }

    @Override
    public String toString() {
        return "ViewTreeInfo [xpath: " + xpath
                +", resourceId: " + resourceId
                +", className: " + className
                +", contentDesc: " + contentDesc
                +", text: " + text
                +", x: " + x
                +", y: " + y
                +", width: " + width
                +", height: " + height + "]";
    }
}
