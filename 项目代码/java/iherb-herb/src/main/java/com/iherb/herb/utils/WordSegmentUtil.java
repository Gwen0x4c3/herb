package com.iherb.herb.utils;

import org.springframework.stereotype.Component;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordSegmentUtil {
    private Configuration config;
    private List<String> herbNames = new ArrayList<>();

    public WordSegmentUtil() {
        this.init();
    }

    public void init() {
        config = DefaultConfig.getInstance();
        boolean res = this.readHerbNames();
        if (!res) {
            throw new RuntimeException("读取失败");
        }
        Dictionary.initial(config);
        Dictionary.getSingleton().addWords(herbNames);
    }

    private boolean readHerbNames() {
        BufferedReader reader = null;
        InputStream in = WordSegmentUtil.class.getClassLoader().getResourceAsStream("words.dic");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                herbNames.add(line);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> extractHerbNames(String text) {
        StringReader reader = new StringReader(text);
        IKSegmenter ikSegmenter = new IKSegmenter(reader, config);
        Lexeme lex;
        List<String> result = new ArrayList<>();
        try {
            while ((lex = ikSegmenter.next()) != null) {
                System.out.println("词 " + lex);
                if (herbNames.contains(lex.getLexemeText())) {
                    result.add(lex.getLexemeText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.forEach(word -> {
            System.out.print(word + ", ");
        });
        return result;
    }
}
