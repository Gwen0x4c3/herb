package com.iherb.user.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iherb.user.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iherb.user.entity.vo.CommentVo;
import com.iherb.user.entity.vo.DiscussionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:56
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {

    Page<CommentVo> listComments(long id, int type, int sort, Page<CommentVo> page);

}
