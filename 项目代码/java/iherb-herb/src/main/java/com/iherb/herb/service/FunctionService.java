package com.iherb.herb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.herb.entity.FunctionEntity;
import com.iherb.herb.entity.vo.FunctionVo;

import java.util.List;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
public interface FunctionService extends IService<FunctionEntity> {

    List<FunctionVo> listWithTree();
    FunctionVo listWithTree(Long functionId);

    PageUtils queryPage(Long curPage, Long limit, String key);

    List<FunctionEntity> searchFunctions(Long[] ids);


}

