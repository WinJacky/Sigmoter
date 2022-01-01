package test.java;

import main.java.core.Word2Vec;
import main.java.domain.WordEntry;
import main.java.utils.WordsSplit;

import java.util.List;
import java.util.Set;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/12/29 20:09
 */
public class Word2VecTest {
    public static void main(String[] args) {

        // 测试加载词向量文件
        Word2Vec w2v = new Word2Vec("nlp/src/main/resources/GloVe/glove.6B.100d.txt");

        // 测试获取某个词的词向量
        float[] f = w2v.getWordVector("username");
        for (float ff: f) {
            System.out.println(ff);
        }

        // 测试计算两个词的语义相似度
        System.out.println(w2v.getSimWith2Words("user","username"));

        // 测试获取语义相关词
        Set<WordEntry> simiNWords = w2v.getTopNSimilarWords("username", 5);
        for (WordEntry entry : simiNWords) {
            System.out.println(entry);
        }

        // 测试分词
        String sentence = "I am    a undergraduate的student_in=?!South   East+University!";
        List<String> words = WordsSplit.getWords(sentence);
        for (String word : words) {
            System.out.println(word);
        }
    }
}
