package main.java.dataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/12 22:32
 */
public class Keyword {
    private int lineNumber;
    private List<String> keywords;
    private Set<String> extendSeq;

    public Keyword(int lineNumber, List<String> keywords) {
        this.lineNumber = lineNumber;
        this.keywords = keywords;
        this.extendSeq = new HashSet<>();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Set<String> getExtendSeq() {
        return extendSeq;
    }

    public void setExtendSeq(Set<String> extendSeq) {
        this.extendSeq = extendSeq;
    }

    public void addToExtendSeq(List<String> words) {
        this.extendSeq.addAll(words);
    }
}
