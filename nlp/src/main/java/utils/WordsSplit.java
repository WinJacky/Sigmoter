package main.java.utils;

import org.ansj.domain.Term;
import org.ansj.recognition.impl.FilterRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.ArrayList;
import java.util.List;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/12/29 19:58
 */
public class WordsSplit {
    private static FilterRecognition filter = new FilterRecognition();

    static {
        filter.insertStopWord(" ", "　", ",", "，", ".", "。", ":", "：", "；", ";", "'", "‘", "’", "“", "”", "<", ">", "《", "》", "[", "]", "【", "】", "(", ")", "（", "）", "{", "}", "-", "_", "=", "?", "？", "!", "！", "&", "|", "\"", "/", "@", "©", "#", "¥", "￥", "+", "的");
    }

    /**
     * 获取分词结果
     * @param sentence 待分词的句子
     * @return 分词结果
     */
    public static List<String> getWords(String sentence) {
        List<Term> termList = ToAnalysis.parse(sentence).recognition(filter).getTerms();
        List<String> wordList = new ArrayList<>();
        for (Term wordTerm : termList){
            wordList.add(wordTerm.getName());
        }
        return wordList;
    }
}
