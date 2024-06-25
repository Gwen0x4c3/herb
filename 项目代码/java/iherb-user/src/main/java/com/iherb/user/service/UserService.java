package com.iherb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.user.entity.UserEntity;

import java.util.Map;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

}

