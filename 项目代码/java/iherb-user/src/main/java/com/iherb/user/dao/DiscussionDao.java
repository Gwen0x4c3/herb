package com.iherb.user.dao;

import com.iherb.user.entity.DiscussionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iherb.user.entity.vo.DiscussionVo;
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
public interface DiscussionDao extends BaseMapper<DiscussionEntity> {

    DiscussionVo getDiscussion(Long id);

    @Update("update t_discussion set keywords=#{keywords} where id=#{id}")
    int updateKeywords(Long id, String keywords);
}
