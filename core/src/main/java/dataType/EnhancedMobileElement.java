package main.java.dataType;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

public class EnhancedMobileElement extends Statement {

    /* Hierarchy layout info */
    private AppiumLocator locator;
    private Point coordinate;
    private Dimension dimension;
    private String xpath;
    private String className;
    private String resourceId;
    private String contentDesc;
    private String text;
    private boolean checkable;
    private boolean clickable;
    private boolean scrollable;
    private boolean focusable;
    private boolean longClickable;

    public EnhancedMobileElement() {
        super();
    }

    public EnhancedMobileElement(int line) {
        super(line);
    }

    public AppiumLocator getLocator() {
        return locator;
    }

    public void setLocator(AppiumLocator locator) {
        this.locator = locator;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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

    @Override
    public String toString() {
        if (getAction() == null) {
            return null;
        } else if (getAction().equals(AppiumAction.SendKeys)) {
            return "driver.findElement" + getLocator() + "." + getAction().getValue() + "(\"" + getValue() + "\")";
        } else if (getAction().equals(AppiumAction.Click) || getAction().equals(AppiumAction.Clear)) {
            return "driver.findElement" + getLocator() + "." + getAction().getValue() + "()";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EnhancedMobileElement) {
            return this.locator.equals(((EnhancedMobileElement) obj).getLocator());
        }
        return false;
    }
}
