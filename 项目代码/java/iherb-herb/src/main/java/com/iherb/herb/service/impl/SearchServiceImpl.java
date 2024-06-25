package com.iherb.herb.service.impl;

import com.alibaba.fastjson.JSON;
import com.iherb.common.constant.EsConstant;
import com.iherb.herb.entity.es.Herb;
import com.iherb.herb.entity.es.Prescription;
import com.iherb.herb.entity.es.SearchResult;
import com.iherb.herb.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    public static final Integer ROWS = 10;

    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SearchResult search(String keyword, Integer type, Integer page, String ticket) {
        if (StringUtils.isNotBlank(ticket)) {
            //保存用户搜索历史
            String historyKey = "search:history:" + type + ":" + ticket;
            ListOperations listOperations = redisTemplate.opsForList();
            listOperations.remove(historyKey, 1, keyword);
            if (listOperations.size(historyKey) > 10) {
                listOperations.rightPop(historyKey);
            }
            listOperations.leftPush(historyKey, keyword);
        }
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        String redisKey = "search:hot:" + type;
        if (type == EsConstant.SEARCH_TYPE_HERB) {
            zSetOperations.incrementScore(redisKey, keyword, 1);
            return searchHerb(keyword, page);
        } else if (type == EsConstant.SEARCH_TYPE_PRESCRIPTION) {
            zSetOperations.incrementScore(redisKey, keyword, 1);
            return searchPrescription(keyword, page);
        }
        return null;
    }

    @Override
    public List<String> getHotWords(int type) {
        String key = "search:hot:" + type;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set<String> hotWords = zSetOperations.reverseRange(key, 0, 9);
        hotWords.forEach(System.out::println);
        if (hotWords == null) {
            return Collections.emptyList();
        }
        long size = zSetOperations.size(key);
        if (size > 10) {
            zSetOperations.removeRange(key, 0, size - 10);
        }
        return new ArrayList<>(hotWords);
    }

    @Override
    public List<String> getSearchHistory(int type, String ticket) {
        String key = "search:history:" + type + ":" + ticket;
        List<String> list = redisTemplate.opsForList().range(key, 0, 10);
        System.out.println(list);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public void clearSearchHistory(String ticket) {
        String key = "search:history:{0}:" + ticket;
        redisTemplate.delete(key.replace("{0}", "0"));
        redisTemplate.delete(key.replace("{0}", "1"));
    }

    @Override
    public List<String> searchForHints(String keyword, Integer type) {
        SearchRequest request = null;
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(keyword);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .from(0)
                .size(7)
                .query(query);
        if (type == EsConstant.SEARCH_TYPE_HERB) {
            request = new SearchRequest(EsConstant.HERB_INDEX);
            query.field("name", 3)
                    .field("name.pinyin", 1.5f)
                    .field("alias", 2)
                    .field("alias.pinyin", 1);
        } else if (type == EsConstant.SEARCH_TYPE_PRESCRIPTION) {
            request = new SearchRequest(EsConstant.PRESCRIPTION_INDEX);
            query.field("name", 3)
                    .field("ingredients", 2)
                    .field("symptom", 1);
        }
        searchSourceBuilder.query(query);
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<String> list = new ArrayList<>();
            response.getHits().forEach(hit -> {
                Map<String, Object> map = hit.getSourceAsMap();
                String name = (String) map.get("name");
                list.add(name);
            });
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private SearchResult searchHerb(String keyword, int page) {
        SearchRequest request = new SearchRequest(EsConstant.HERB_INDEX);
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(keyword)
                .field("name", 3)
                .field("name.pinyin", 1.5f)
                .field("alias", 2)
                .field("alias.pinyin", 1);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(query)
//                .fetchSource(null, "text")
                .from((page - 1) * ROWS)
                .size(ROWS)
                .highlighter(SearchSourceBuilder.highlight()
                        .preTags("<em class='highlight'>")
                        .postTags("</em>")
                        .field("name"));
        request.source(searchSourceBuilder);
        System.out.println();
        List<Herb> list = new ArrayList<>();
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            response.getHits().forEach(hit -> {
                Herb herb = JSON.parseObject(hit.getSourceAsString(), Herb.class);
                Map<String, HighlightField> highlightMap = hit.getHighlightFields();
                //高亮中药名
                HighlightField highlightField = highlightMap.get("name");
                if (highlightField != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Text fragment : highlightField.getFragments()) {
                        builder.append(fragment.toString());
                        System.out.println("高亮" + fragment);
                    }
                    herb.setName(builder.toString());
                }
                System.out.println(herb);
                list.add(herb);
            });
            int totalPage = (int) Math.floor(1. * response.getHits().getTotalHits().value / ROWS);
            return new SearchResult(totalPage == 0 ? 0 : page, totalPage, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResult searchPrescription(String keyword, Integer page) {
        SearchRequest request = new SearchRequest(EsConstant.PRESCRIPTION_INDEX);
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(keyword, "name");
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(query)
                .from((page - 1) * ROWS)
                .size(ROWS)
                .highlighter(SearchSourceBuilder.highlight().field("name"));
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<Prescription> list = new ArrayList<>();
            response.getHits().forEach(hit -> {
                Prescription prescription = JSON.parseObject(hit.getSourceAsString(), Prescription.class);
                list.add(prescription);
            });
            int totalPage = (int) Math.floor(1. * response.getHits().getTotalHits().value / ROWS);
            return new SearchResult(totalPage == 0 ? 0 : page, totalPage, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





}
