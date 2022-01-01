package main.java.dataType;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/6/30 9:44
 */
public class KeyText {
    private int lineNumber;
    private String text;
    private String type;

    public KeyText(int lineNumber, String text){
        this.lineNumber = lineNumber;
        this.text = text;
    }

    public KeyText(int lineNumber, String text, String type){
        this(lineNumber, text);
        this.type = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
