package main.java.domain;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/12/29 18:00
 */
public class WordEntry implements Comparable<WordEntry>{
    public String name;
    public float score;

    public WordEntry(String name, float score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(WordEntry o) {
        if (this.score < o.score) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WordEntry && name.equals(((WordEntry) obj).name);
    }

    @Override
    public String toString() {
        return this.name + "\t" + score;
    }
}
