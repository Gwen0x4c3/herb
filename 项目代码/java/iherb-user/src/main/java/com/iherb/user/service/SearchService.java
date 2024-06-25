package com.iherb.user.service;

import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.entity.es.Article;

import java.util.List;

public interface SearchService {

    void addArticle(ArticleEntity entity);

    void addArticles(List<ArticleEntity> entities);

    List<Article> getArticles(List<String> keywords, Long[] excludeIds);

    List<Article> searchArticles(String keyword, int page);

}
