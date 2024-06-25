package com.iherb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.entity.es.Article;
import com.iherb.user.entity.vo.ArticleVo;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
public interface ArticleService extends IService<ArticleEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

    List<Article> recommend(String userId, Long[] excludeIds);

    ArticleVo getArticle(Long id, String userId);

    void saveArticle(ArticleEntity article, String userId);

    void resetKeyword(ArticleEntity article);

    void incrViewCount(Long id);

    void incrCommentCount(Long id);

}

