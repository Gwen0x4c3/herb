package com.iherb.herb.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import ai.djl.modality.Classifications;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.iherb.common.constant.EsConstant;
import com.iherb.common.constant.ResultConstant;
import com.iherb.herb.entity.es.ImageSearchResult;
import com.iherb.herb.entity.vo.*;
import com.iherb.herb.service.FunctionService;
import com.iherb.herb.service.SearchService;
import com.iherb.herb.utils.HerbRecUtil;
import com.iherb.herb.utils.WordSegmentUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.iherb.herb.entity.HerbEntity;
import com.iherb.herb.service.HerbService;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.R;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
@Api(tags="中药接口")
@RestController
@RequestMapping("/herb/herb")
@CrossOrigin
public class HerbController {
    @Autowired
    private HerbService herbService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private HerbRecUtil herbRecUtil;
    @Autowired
    private WordSegmentUtil wordSegmentUtil;
    @Autowired
    private SearchService searchService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取数据列表。拼接参数page=?&limit=?。" +
            "page--第几页，从1开始。limit--一页多少条数据。key--关键词，可选参数。")
    public R list(@RequestParam("page") Long curPage, @RequestParam("limit") Long limit, String key){
        PageUtils page = herbService.queryPage(curPage, limit, key);
        return R.ok().data("page", page);
    }

    @GetMapping("/listByFunctionId")
    public R listByFunctionId(@RequestParam Map<String, Object> params) {
        PageUtils page = herbService.queryPageByFunctionId(params);
        return R.ok().data("page", page);
    }

    @GetMapping("/listByFunction")
    public R listByFunction(Long functionId) {
        List<FunctionVo> list;
        if (functionId != null) {
            FunctionVo functionVo = herbService.listByFunction(functionId);
            return R.ok().data("data", functionVo);
        } else {
            list = herbService.listByFunction();
        }
        return R.ok().data("list", list);
    }

    @GetMapping("/listByTropism")
    public R listByTropism(Long tropismId) {
        if (tropismId != null) {
            TropismVo data = herbService.listByTropism(tropismId);
            return R.ok().data("data", data);
        } else {
            List<TropismVo> list = herbService.listByTropism();
            return R.ok().data("list", list);
        }
    }

    @GetMapping("/searchName")
    public R searchHerbNames(String name, Boolean alias, Long[] excludeIds) {
        List<String> herbNames = searchService.searchForHints(name, EsConstant.SEARCH_TYPE_HERB);
        System.out.println(herbNames);
        /*
        QueryWrapper<HerbEntity> wrapper = new QueryWrapper<HerbEntity>()
                .like("name", name)
                .select("id", "name");
        if (alias != null && alias) {
            wrapper.like("alias", name);
        }
        List<HerbEntity> list = herbService.list(wrapper);
        */
        if (CollectionUtils.isEmpty(herbNames)) {
            return R.ok().data("list", Collections.emptyList());
        }
        QueryWrapper<HerbEntity> wrapper = new QueryWrapper<HerbEntity>()
                .in("name", herbNames)
                .select("id", "name");
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            wrapper.notIn("id", excludeIds);
        }
        List<HerbEntity> list = herbService.list(wrapper);
        return R.ok().data("list", list);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("herb:herb:info")
    public R info(@PathVariable("id") Long id){
        HerbVo herbVo = herbService.getHerb(id);

        return R.ok().data("herb", herbVo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("herb:herb:save")
    public R save(@RequestBody HerbVo vo){
        herbService.saveHerb(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("herb:herb:update")
    public R update(@RequestBody HerbEntity herb){
		herbService.updateById(herb);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    //@RequiresPermissions("herb:herb:delete")
    public R delete(@RequestBody Long[] ids){
        System.out.println(ids);
		herbService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/recognize")
    public R recognizeHerb(MultipartFile file) {
        if (file.isEmpty()) {
            return R.error(ResultConstant.ERROR, "输入图像无法识别");
        }
        try {
            List<Classifications.Classification> predict = herbRecUtil.predict(file.getInputStream());
            List<HerbClassification> result = new ArrayList<>();
            predict.forEach(p -> {
                HerbEntity herb = herbService.getOne(new QueryWrapper<HerbEntity>().eq("name", p.getClassName()));
                if (herb == null) {
                    return;
                }
                HerbClassification classification = new HerbClassification();
                BeanUtils.copyProperties(herb, classification);
                classification.setProbability(p.getProbability());
                result.add(classification);
            });
            return R.ok().data("list", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.ok();
    }

    @PostMapping("/analyze")
    public R analyzeHerbs(Long[] ids) {
        AnalyzeVo analyzeVo = herbService.analyzeHerbs(ids);
        return R.ok().data("result", analyzeVo);
    }

    @PostMapping("/imageSearch")
    public R imageSearch(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.error(R.ARGUMENT_INVALID, "图片不可为空");
        }
        try {
            List<ImageSearchResult> list = herbService.imageSearch(file.getInputStream());
            return R.ok().data("list", list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
