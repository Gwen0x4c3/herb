package com.iherb.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.entity.CommentEntity;
import com.iherb.user.entity.DiscussionEntity;
import com.iherb.user.entity.es.Article;
import com.iherb.user.service.ArticleService;
import com.iherb.user.service.CommentService;
import com.iherb.user.service.DiscussionService;
import com.iherb.user.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class IherbUserApplicationTests {

    @Autowired
    private CommentService commentService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private DiscussionService discussionService;

    @Test
    void contextLoads() {
    }

    @Test
    void addComments() {
        List<CommentEntity> list = new ArrayList<>();
        String[] contents = {"好好好", "你说的对，收藏了。", "我觉得可以的",
                "请勿挖坟好吗，陈年老贴了。"};
        Date now = new Date();
        for (int i = 6; i < contents.length + 6; i++) {
            CommentEntity comment = new CommentEntity();
            comment.setContent(contents[i - 6]);
            comment.setCreateTime(new Date(now.getTime() + i * 10000));
            comment.setType(0);
            comment.setTargetId(1l);
            if (i == 0)
                comment.setUserId("ohuew5IFk8MBDycdq_Iv8b94kzHw");
            else
                comment.setUserId("openid" + i);
            list.add(comment);
        }
        System.out.println(list);
        commentService.saveBatch(list);
    }

    @Test
    void listComments() {
        commentService.listComments(1l, 0, 1l, 1);

    }

    @Test
    void addArticlesToEs() {
        List<ArticleEntity> list = articleService.list();
        searchService.addArticles(list);
    }

    @Test
    void searchArticle() {
        List<String> keywords = new ArrayList<>();
        keywords.add("胃疼");
        keywords.add("咳嗽");
        searchService.getArticles(keywords, new Long[]{8L, 12L, 18L, 20L, 30L});
    }

    @Test
    void updateKeywords() {
        List<ArticleEntity> list = articleService.list();
        list.forEach(article -> {
            articleService.resetKeyword(article);
        });
    }

    @Test
    void updateKeywords1() {
        List<DiscussionEntity> list = discussionService.list();
        list.forEach(discussion -> {
            discussionService.resetKeyword(discussion);
        });
    }

    @Test
    void searchArticles() {
        List<Article> list = searchService.searchArticles("睡不着", 1);
        list.forEach(System.out::println);
    }

}
