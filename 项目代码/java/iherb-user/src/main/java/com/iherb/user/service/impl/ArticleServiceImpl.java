package com.iherb.user.service.impl;

import com.iherb.user.entity.es.Article;
import com.iherb.user.entity.vo.ArticleVo;
import com.iherb.user.service.SearchService;
import com.iherb.user.util.KeywordUtil;
import joptsimple.internal.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.user.dao.ArticleDao;
import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.service.ArticleService;


@Service("articleService")
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, ArticleEntity> implements ArticleService {

    @Autowired
    private SearchService searchService;

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private KeywordUtil keywordUtil;

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<ArticleEntity> page = this.page(
                new Query<ArticleEntity>().getPage(curPage, limit),
                new QueryWrapper<ArticleEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<Article> recommend(String userId, Long[] excludeIds) {
        int num = 4 + RandomUtils.nextInt(10);
        Set<String> keywords = redisTemplate.opsForZSet().reverseRange("interest:" + userId, 0, num);
        List<Article> list;
        if (keywords == null) {
            list = searchService.getArticles(Collections.emptyList(), excludeIds);
        } else {
            list = searchService.getArticles(new ArrayList<>(keywords), excludeIds);
        }
        //如果查出来的文章数太少，就重置用户的兴趣
        if (list.size() < 7) {
            redisTemplate.delete("interest:" + userId);
        }
        return list;
    }

    @Override
    public ArticleVo getArticle(Long id, String userId) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        ArticleVo article = (ArticleVo) valueOperations.get("article:" + id);
        Integer viewCount = (Integer) valueOperations.get("article:view:" + id);
        if (article != null) {
            article.setViewCount(viewCount + 1);
            valueOperations.increment("article:view:" + id);
        } else {
            article = this.baseMapper.getArticle(id);
            valueOperations.set("article:" + id, article, 24, TimeUnit.HOURS);
            valueOperations.set("article:view:" + id, article.getViewCount(), 24, TimeUnit.HOURS);
        }

        //分析keyword，设置用户interest
        String keywordsStr = article.getKeywords();
        if (StringUtils.isBlank(keywordsStr)) {
            return article;
        }
        String[] keywords = keywordsStr.split(",");
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String redisKey = "interest:" + userId;
        long size = zSetOperations.size(redisKey);
        if (size > 15) {
            zSetOperations.removeRange(redisKey, 0, size - 15);
        }
        Random random = new Random();
        int length = keywords.length;
        int num = 1 + random.nextInt(length);
        for (int i = 0; i < length && i < num; i++) {
            zSetOperations.incrementScore("interest:" + userId, keywords[i], 0.1 + (length - i) * random.nextDouble());
        }
        return article;
    }

    @Override
    public void saveArticle(ArticleEntity article, String userId) {
        article.setUserId(userId);
        article.setCommentCount(0);
        article.setViewCount(0);

        List<String> keywords = extractKeywords(article.getContent());
        String keywordStr = String.join(",", keywords);
        article.setKeywords(keywordStr);

        article.setCreateTime(new Date());
        article.setModifyTime(new Date());

        System.out.println("文章：" + article);
        this.save(article);
    }

    @Override
    public void resetKeyword(ArticleEntity article) {
        List<String> keywords = extractKeywords(article.getContent());
        String keywordsStr = Strings.join(keywords, ",");
        System.out.println("Keywords: " + keywordsStr);
        this.baseMapper.updateKeywords(article.getId(), keywordsStr);
    }

    @Override
    public void incrViewCount(Long id) {
        this.baseMapper.incrViewCount(id);
    }

    @Override
    public void incrCommentCount(Long id) {
        this.baseMapper.incrCommentCount(id);
    }

    private List<String> extractKeywords(String content) {
        Pattern pattern = Pattern.compile("<p>([^<>]*?)</p>");
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            if (matcher.group(1).length() > 0)
                sb.append(matcher.group(1))
                        .append("\n");
        }
        return keywordUtil.getKeywords(sb.toString(), 5);
    }

}
