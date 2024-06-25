package com.iherb.herb.utils;

import com.alibaba.fastjson.JSONObject;
import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.entity.PrescriptionEntity;
import com.iherb.herb.entity.TextEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParseUtil {

    public static void main(String[] args) {
        for (int i = 1; i <= 20; i++) {
            System.out.println("第" + i + "页");
            String url = "http://www.zhongyoo.com/fangji/page_" + i + ".html";
            try {
                Document document = Jsoup.parse(new URL(url), 3000);
                Elements lis = document.getElementsByClass("listbox").get(0).getElementsByTag("li");
                lis.forEach(li -> {
                    String s = li.getElementsByTag("a").get(0).attr("href");
                    PrescriptionEntity prescription = HtmlParseUtil.parsePrescriptionPage(s);
                    if (prescription.getFunctions() == null || prescription.getIngredient() == null ||
                            prescription.getMethod() == null || prescription.getSymptom() == null || prescription.getSource() == null) {
                        System.out.println(prescription);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> parseCategoryPage(String letter) {
        String url = "http://www.zhongyoo.com/pinyin/" + letter;
        Document doc = null;
        List<String> urls = new ArrayList<>();
        try {
            doc = Jsoup.parse(new URL(url), 3000);
            Elements uls = doc.getElementsByClass("pyBoxCon").get(0).getElementsByTag("ul");
            uls.forEach(ul -> {
                Elements lis = ul.getElementsByTag("li");
                lis.forEach(li -> {
                    urls.add(li.getElementsByTag("a").get(0).attr("href"));
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    /**
     * 解析中药页面，返回页面内容的json格式文本
     * @param url 页面url
     * @return
     */
    public static String parseHerbPage(String url) {
        return parseHerbPage(url, null);
    }

    /**
     * 解析中药页面，返回页面内容的json格式文本
     * @param url 页面url
     * @param herb 为herb填入名称、别名
     * @return
     */
    public static String parseHerbPage(String url, HerbEntity herb) {
        Document doc = null;
        try {
            doc = Jsoup.parse(new URL(url), 3000);
            Element gaishu = doc.getElementsByClass("gaishu").get(0);

            String herbName = gaishu.getElementsByClass("title").get(0).text();
            AtomicReference<String> alias = new AtomicReference<>("");

            Element text = gaishu.getElementsByClass("text").get(0);
            Elements lines = text.getElementsByTag("p");

            Elements img = text.getElementsByTag("img");
            String imgSrc = "";
            if (img.size() != 0) {
                imgSrc = img.get(0).attr("abs:src");
            }

            List<JSONObject> list = new ArrayList<>();
            Pattern pattern = Pattern.compile("^【(.*?)】(.*)");
            JSONObject json = new JSONObject(true);
            lines.forEach(l -> {
                if (l.text().length() == 0) {
                    return;
                }
                //先把全角空格替换
                String content = l.text().replaceAll(String.valueOf((char)(12288)), "");
                JSONObject object = null;
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    object = new JSONObject();
                    object.put("title", matcher.group(1));
                    object.put("content", matcher.group(2));
                    if (matcher.group(1).equals("别名")) {
                        alias.set(getAlias(matcher.group(2).replace("。", "")));
                    }
                    list.add(object);
                } else {
                    object = list.get(list.size() - 1);
                    String pre = object.getString("content");
                    pre += "\n" + content;
                    object.put("content", pre);
                }
            });
            list.forEach(attr -> {
                json.put(attr.getString("title"), attr.getString("content"));
            });

            System.out.println("=============================");
            System.out.println("中药名：" + herbName);
            System.out.println("别名:" + alias);
            System.out.println(json);

            if (herb != null) {
                herb.setName(herbName);
                herb.setAlias(alias.toString());
                herb.setImage(imgSrc);
                herb.setCreateTime(new Date());
                herb.setModifyTime(new Date());
            }
            return json.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAlias(String source) {
        String[] aliases = source.split("、");
        StringBuilder result = new StringBuilder();
        result.append(aliases[0]);
        for (int i = 1; i < aliases.length; i++) {
            result.append(",").append(aliases[i]);
        }
        return result.toString();
    }

    /**
     * 解析功效-中药页面
     * @param url 功效页面url
     * @return 返回 {功效名,中药名称列表} map
     */
    public static Map<String, List<String>> parseFunctionPage(String url) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 3000);
            Elements dls = document.getElementsByTag("dl");
            dls.forEach(dl -> {
                String functionName = dl.getElementsByTag("dt").get(0).text();
                List<String> herbNames = new ArrayList<>();
                Elements lis = dl.getElementsByTag("li");
                lis.forEach(li -> {
                    herbNames.add(li.text());
                });
                System.out.println("功效：" + functionName);
                System.out.println("中药：" + herbNames);
                map.put(functionName, herbNames);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, List<String>> parseTropismPage(String url) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 3000);
            Element dl = document.getElementsByTag("dl").get(0);
            Elements lis = dl.getElementsByTag("li");
            String tropismName = dl.getElementsByTag("dt").get(0).text();
            List<String> herbNames = new ArrayList<>();
            lis.forEach(li -> {
                herbNames.add(li.text());
            });
            map.put(tropismName, herbNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static PrescriptionEntity parsePrescriptionPage(String url) {
        PrescriptionEntity prescription = new PrescriptionEntity();
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 3000);
            String name = document.getElementsByClass("art_box").get(0).getElementsByTag("h1").get(0).text();
            prescription.setName(name);
            Elements lines = document.getElementById("contentText").getElementsByTag("p");
            lines.forEach(line -> {
                String text = line.text().replaceAll(String.valueOf((char)(12288)), "");;
                if (!text.startsWith("【"))
                    return;
                int idx = text.indexOf("】");
                String title = text.substring(1, idx);
                String content = text.substring(idx + 1);
                switch (title) {
                    case "方剂出处":
                    case "方剂出自":
                    case "方剂名":
                    case "方名":
                        prescription.setSource(content);
                        break;
                    case "组成用法":
                    case "组成":
                    case "配方组成":
                        prescription.setIngredient(content);
                        break;
                    case "用法":
                    case "用法用量":
                        prescription.setMethod(content);
                        break;
                    case "功效":
                    case "功效作用":
                        prescription.setFunctions(content);
                        break;
                    case "功效主治":
                    case "主治应用":
                    case "主治":
                        prescription.setSymptom(content);
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prescription;
    }
}
