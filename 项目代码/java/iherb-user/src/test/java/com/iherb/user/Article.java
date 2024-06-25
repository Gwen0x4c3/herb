package com.iherb.user;

import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.service.ArticleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = IherbUserApplication.class)
@RunWith(SpringRunner.class)
public class Article {

    private static String BASE_URL = "https://www.zhzyw.com";

    @Autowired
    private ArticleService articleService;

    @Test
    public void test1() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 31; i <= 50; i++) {
            System.out.println("========第" + i + "页========");
            List<String> urls = parseListPage(i);
            urls.forEach(url -> {
                try {
                    ArticleEntity article = parseArticlePage(url);
                    articleService.saveArticle(article, "ohuew5IFk8MBDycdq_Iv8b94kzHw");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        long end = System.currentTimeMillis();
        System.out.println("Time consumed: " + (end - start) / 1000 + "秒");
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) {
            System.out.println("========第" + i + "页========");
            List<String> urls = parseListPage(i);
            urls.forEach(url -> {
                try {
                    ArticleEntity article = parseArticlePage(url);
                    System.out.println(article);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        long end = System.currentTimeMillis();
        System.out.println("Time consumed: " + (end - start) / 1000 + "秒");
    }

    public static List<String> parseListPage(int page) throws Exception {
        String url = "https://www.zhzyw.com/zycs/yaxd/index" +
                (page == 1 ? "" : "_" + page) + ".html";
        System.out.println("======正在爬: " + url);
        Document doc = Jsoup.parse(new URL(url), 5000);
        List<String> list = new ArrayList<>();
        Elements as = doc.getElementsByClass("ullist01").get(0)
                .getElementsByTag("A");
        as.forEach(a -> {
            String href = a.attr("href");
            list.add(BASE_URL + href);
        });
        return list;
    }

    public static ArticleEntity parseArticlePage(String url) throws Exception {
        Document doc = Jsoup.parse(new URL(url), 10000);
        Element title = doc.getElementsByTag("H1").get(0);
        Elements ps = doc.getElementsByClass("webnr").get(0)
                .getElementsByTag("P");
        ArticleEntity article = new ArticleEntity();
        StringBuilder sb = new StringBuilder();
        ps.forEach(p -> {
            String line = p.toString().replaceAll("</?[^pi/]+?\\s?.*?>", "");
            sb.append(line);
        });
        article.setTitle(title.text());
        article.setContent(sb.toString());
        return article;
    }
}
