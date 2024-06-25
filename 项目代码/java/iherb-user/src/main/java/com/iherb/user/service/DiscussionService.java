package com.iherb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.user.entity.DiscussionEntity;
import com.iherb.user.entity.vo.DiscussionVo;

import java.util.Map;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
public interface DiscussionService extends IService<DiscussionEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

    void saveDiscussion(DiscussionEntity discussion, String userId);

    PageUtils getDiscussions(Long page);

    DiscussionVo getDiscussion(Long id, String userId);

    void resetKeyword(DiscussionEntity discussion);
}

