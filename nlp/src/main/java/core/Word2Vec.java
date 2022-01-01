package main.java.core;

import main.java.domain.WordEntry;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author feisher
 * @version 1.0
 * @date 2021/12/29 17:33
 */
public class Word2Vec {

    private static Logger logger = Logger.getLogger(Word2Vec.class);

    private HashMap<String, float[]> wordsMap = new HashMap<>();

    // 模型总词数
    private int wordsNum;

    // 向量维度大小
    private int size;

    private String modelPath;

    /**
     * 加载词向量
     * @param modelPath
     */
    public Word2Vec(String modelPath){
        this.modelPath = modelPath;

        // 加载的词向量文件需要以 txt 格式表示，且其首行应包含此文件包含的词向量个数及维度大小
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.modelPath), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            String[] headers = line.split("\\s+");

            wordsNum = Integer.parseInt(headers[0].trim());
            size = Integer.parseInt(headers[1].trim());

            float dim;
            String key;
            float[] value;
            for (int i = 0; i < wordsNum; i++) {
                double len = 0;
                line = br.readLine().trim();
                String[] params = line.split("\\s+");
                if (params.length != size + 1) {
                    logger.warn("词向量格式不规范（可能是单词含有空格），将被跳过：" + line);
                    --wordsNum;
                    --i;
                    continue;
                }
                key = params[0];
                value = new float[size];
                for (int j = 0; j < size; j++) {
                    dim = Float.parseFloat(params[j + 1]);
                    len += dim * dim;
                    value[j] = dim;
                }
                len = Math.sqrt(len);
                for (int j = 0; j < size; j++) {
                    value[j] /= len;
                }
                wordsMap.put(key, value);
            }
        }catch (IOException e){
            e.printStackTrace();
            logger.error("词向量文件加载失败！");
        }
    }

    /**
     * 获取某个词的向量
     */
    public float[] getWordVector(String word) {
        return wordsMap.get(word);
    }

    /**
     * 计算两个词之间的相似度
     */
    public double getSimWith2Words(String word1, String word2) {
        if (word1.equals(word2)) {
            return 1;
        }
        float[] vector1 = wordsMap.get(word1);
        float[] vector2 = wordsMap.get(word2);
        if (vector1 == null || vector2 == null) {
            return 0.0;
        }
        double sim = 0;
        for (int i = 0; i < vector1.length; i++) {
            sim += vector1[i] * vector2[i];
        }
        return sim;
    }

    /**
     * 获取与查询词语义最相关的 N 个词
     */
    public Set<WordEntry> getTopNSimilarWords(String queryWord, int N) {
        float[] srcVector = wordsMap.get(queryWord);
        if (srcVector == null) {
            return Collections.emptySet();
        }
        TreeSet<WordEntry> result = new TreeSet<>();
        double min = Float.MIN_VALUE;
        for (Map.Entry<String, float[]> entry : wordsMap.entrySet()) {
            if (entry.getKey().equals(queryWord)) {
                continue;
            }
            float[] vector = entry.getValue();
            float dist = 0;
            for (int i = 0; i < vector.length; i++) {
                dist += srcVector[i] * vector[i];
            }

            if (dist > min) {
                while (N <= result.size()) {
                    result.pollLast();
                }
                result.add(new WordEntry(entry.getKey(), dist));
                min = result.last().score;
            }
        }

        return result;
    }

    public int getWordsNumber() {
        return wordsNum;
    }

    public int getSize() {
        return size;
    }
}
