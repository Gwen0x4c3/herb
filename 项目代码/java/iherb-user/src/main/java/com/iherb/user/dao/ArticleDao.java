package com.iherb.user.dao;

import com.iherb.user.entity.ArticleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iherb.user.entity.vo.ArticleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleEntity> {

    @Update("update t_article set view_count=view_count+1 where id=#{id}")
    void incrViewCount(Long id);

    @Update("update t_article set comment_count=comment_count+1 where id=#{id}")
    void incrCommentCount(Long id);

    ArticleVo getArticle(Long id);

    @Update("update t_article set keywords=#{keywords} where id=#{id}")
    int updateKeywords(Long id, String keywords);
}
