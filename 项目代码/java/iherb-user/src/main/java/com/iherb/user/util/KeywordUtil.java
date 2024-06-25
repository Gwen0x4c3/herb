package com.iherb.user.util;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class KeywordUtil {
    Configuration cfg;
    List<String> expandWords = new ArrayList<>();

    /**
     * 每个词的最小长度
     */
    private static final int MIN_LEN = 2;

    KeywordUtil() {
        cfg = DefaultConfig.getInstance();
        cfg.setUseSmart(true); //设置useSmart标志位 true-智能切分 false-细粒度切分
        boolean flag = loadKeywords("keywords.dic");
        if (!flag) {
            throw new RuntimeException("读取失败");
        }
        Dictionary.initial(cfg);
        Dictionary.getSingleton().addWords(expandWords); //词典中加入自定义单词
    }

    /**
     * 自己制定额外加载的词典，如果不想要就不用调用
     * @param filenames
     * @return
     */
    private boolean loadKeywords(String... filenames) {
        try {
            for (String filename : filenames) {
                expandWords.addAll(IOUtils.readLines(KeywordUtil.class.getClassLoader().getResourceAsStream(filename),
                        StandardCharsets.UTF_8));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 提取词语，结果将按频率排序
     * @param text 待提取的文本
     * @return 提取出的词
     */
    public List<String> extract(String text) {
        System.out.println(text);
        StringReader reader = new StringReader(text);
        IKSegmenter ikSegmenter = new IKSegmenter(reader, cfg);
        Lexeme lex;
        Map<String, Integer> countMap = new HashMap<>();
        try {
            while ((lex = ikSegmenter.next()) != null) {
                String word = lex.getLexemeText();
                System.out.println(word);
                if (word.length() >= MIN_LEN) { //取出的词至少#{MIN_LEN}个字
                    countMap.put(word, countMap.getOrDefault(word, 0) + 1);
                }
            }
            List<String> result = new ArrayList<>(countMap.keySet());
            //根据词出现频率从大到小排序
            result.sort((w1, w2) -> countMap.get(w2) - countMap.get(w1));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * 提取存在于我扩充词典的词
     * @param num 需要提取的词个数
     * @return
     */
    public List<String> getKeywords(String text, Integer num) {
        List<String> words = extract(text);
        if (num >= words.size()) {
            return words;
        }
        List<String> result = new ArrayList<>();
        int count = 0;
        for (String word : words) {
            if (expandWords.contains(word)) {
                result.add(word);
                if (++count == num) {
                    break;
                }
            }
        }
        //检查提取的关键词是否足够
        if (count < num / 2) {
            for (int i = 0; i < num / 2; i++) {
                if (!result.contains(words)) {
                    result.add(words.get(i));
                }
            }
        }
        return result;
    }

}
