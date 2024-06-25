package com.iherb.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.iherb.common.constant.EsConstant;
import com.iherb.common.constant.RedisKey;
import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.entity.es.Article;
import com.iherb.user.service.SearchService;
import org.apache.commons.lang.math.RandomUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchServiceImpl implements SearchService {

    public static final int ROWS = 10;

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addArticle(ArticleEntity entity) {
        IndexRequest request = new IndexRequest(EsConstant.ARTICLE_INDEX);
        Article article = parseArticle(entity);
        request.id(article.getId().toString())
                .source(JSON.toJSONString(article), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addArticles(List<ArticleEntity> entities) {
        BulkRequest bulkRequest = new BulkRequest(EsConstant.ARTICLE_INDEX);
        entities.forEach(entity -> {
            IndexRequest request = new IndexRequest();
            Article article = parseArticle(entity);
            request.id(article.getId().toString())
                    .source(JSON.toJSONString(article), XContentType.JSON);
            bulkRequest.add(request);
        });
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                throw new RuntimeException("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Article> getArticles(List<String> keywords, Long[] excludeIds) {
        SearchRequest request = new SearchRequest(EsConstant.ARTICLE_INDEX);
        SearchSourceBuilder builder = SearchSourceBuilder.searchSource();

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //排除已经展示的文章的id
        if (excludeIds != null) {
            String[] ids = new String[excludeIds.length];
            for (int i = 0; i < excludeIds.length; i++) {
                ids[i] = excludeIds[i].toString();
            }
            query.mustNot(QueryBuilders.idsQuery().addIds(ids));
        }
        //搜索文章
        int length = keywords.size();
        for (int i = 0; i < length; i++) {
            query.should(QueryBuilders.matchQuery("keywords", keywords.get(i)).boost((length - i) * RandomUtils.nextFloat()));
        }
        builder.query(query)
                .size(ROWS);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Article> searchArticles(String keyword, int page) {
        SearchRequest request = new SearchRequest(EsConstant.ARTICLE_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(keyword);
        query.field("title", 2)
                .field("title.pinyin", 1)
                .field("keywords", 1.5f);

        builder.query(query)
                .from((page - 1) * ROWS)
                .size(ROWS);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private Article parseArticle(ArticleEntity entity) {
        Article article = new Article();
        article.setTitle(entity.getTitle());
        article.setId(entity.getId());
        article.setCover(extractImage(entity.getContent()));
        article.setCreateTime(entity.getCreateTime());
        String[] arr = entity.getKeywords().split(",");
        List<String> keywords = Arrays.asList(arr);
        article.setKeywords(keywords);
        return article;
    }

    private String extractImage(String content) {
        Pattern pattern = Pattern.compile("<img.*src=\"(.*?)\".*>");
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find())
            return "";
        return matcher.group(1);
    }

    private List<Article> parseResponse(SearchResponse response) {
        List<Article> list = new ArrayList<>();
        response.getHits().forEach(hit -> {
            Article article = JSON.parseObject(hit.getSourceAsString(), Article.class);
            Integer viewCount = (Integer) redisTemplate.opsForValue().get(RedisKey.ARTICLE_VIEW
                    .replace("#1", article.getId().toString()));
            if (viewCount == null) {
                article.setViewCount(0);
            } else {
                article.setViewCount(viewCount);
            }
            list.add(article);
        });
        return list;
    }
}
