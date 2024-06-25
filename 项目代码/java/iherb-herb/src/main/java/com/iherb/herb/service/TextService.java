package com.iherb.herb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.herb.entity.TextEntity;

import java.util.Map;

/**
 * 
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-18 11:59:34
 */
public interface TextService extends IService<TextEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);
}

