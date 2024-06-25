package com.iherb.herb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iherb.common.utils.PageUtils;
import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.entity.es.ImageSearchResult;
import com.iherb.herb.entity.vo.AnalyzeVo;
import com.iherb.herb.entity.vo.FunctionVo;
import com.iherb.herb.entity.vo.HerbVo;
import com.iherb.herb.entity.vo.TropismVo;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
public interface HerbService extends IService<HerbEntity> {

    PageUtils queryPage(Long curPage, Long limit, String key);

    PageUtils queryPageByFunctionId(Map<String, Object> params);

    HerbVo getHerb(Long id);

    void saveHerb(HerbVo vo);

    void saveHerb(String json, HerbEntity herb);

    AnalyzeVo analyzeHerbs(Long[] ids);

    void addHerbs();

    List<FunctionVo> listByFunction();

    FunctionVo listByFunction(Long functionId);

    List<TropismVo> listByTropism();

    TropismVo listByTropism(Long tropismId);

    List<ImageSearchResult> imageSearch(InputStream inputStream);
}

