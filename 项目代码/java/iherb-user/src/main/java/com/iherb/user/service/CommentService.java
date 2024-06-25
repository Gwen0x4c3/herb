package com.iherb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.user.entity.CommentEntity;

import java.util.Map;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:56
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

    void addComment(CommentEntity comment);

    PageUtils listComments(Long id, Integer type, Long page, Integer sort);
}

