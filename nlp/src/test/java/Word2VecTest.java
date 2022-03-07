package test.java;

import main.java.core.Word2Vec;
import main.java.domain.WordEntry;
import main.java.utils.WordsSplit;

import java.util.HashSet;
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

        // 测试计算两个词集的语义相似度
        Set<String> set1, set2, set3, set4, set5;
        set1 = new HashSet<>(WordsSplit.getWords("Search…".trim()));
        set2 = new HashSet<>(WordsSplit.getWords("Search YouTube and Library".trim()));
        set3 = new HashSet<>(WordsSplit.getWords("Sort by…".trim()));
        set4 = new HashSet<>(WordsSplit.getWords("Navigate up".trim()));
        set5 = new HashSet<>(WordsSplit.getWords("Note editing".trim()));
        System.out.println(computeSimilarity(w2v, set1, set2));
        System.out.println(computeSimilarity(w2v, set1, set3));
        System.out.println(computeSimilarity(w2v, set1, set4));
        System.out.println(computeSimilarity(w2v, set1, set5));

        // 测试获取某个词的词向量
//        float[] f = w2v.getWordVector("username");
//        for (float ff: f) {
//            System.out.println(ff);
//        }
//
//        // 测试计算两个词的语义相似度
//        System.out.println(w2v.getSimWith2Words("the","theme"));
//
//        // 测试获取语义相关词
//        Set<WordEntry> simiNWords = w2v.getTopNSimilarWords("username", 5);
//        for (WordEntry entry : simiNWords) {
//            System.out.println(entry);
//        }
//
//        // 测试分词
//        String sentence = "I am    a undergraduate的student_in=?!South   East+University!";
//        List<String> words = WordsSplit.getWords(sentence);
//        for (String word : words) {
//            System.out.println(word);
//        }
    }

    private static double computeSimilarity(Word2Vec word2Vec, Set<String> set1, Set<String> set2) {
        double sumScore1 = 0.0;
        for (String s1: set1) {
            double maxScore = 0.0;
            for (String s2: set2) {
                maxScore = Math.max(maxScore, word2Vec.getSimWith2Words(s1, s2));
            }
            sumScore1 += maxScore;
        }
        sumScore1 =  sumScore1 / set1.size();
        return sumScore1;

//        double sumScore2 = 0.0;
//        for (String s1: set2) {
//            double maxScore = 0.0;
//            for (String s2: set1) {
//                maxScore = Math.max(maxScore, word2Vec.getSimWith2Words(s1, s2));
//            }
//            sumScore2 += maxScore;
//        }
//        sumScore2 =  sumScore2 / set2.size();
//
//        return Math.max(sumScore1, sumScore2);
    }
}
