package com.iherb.herb.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.iherb.herb.entity.TropismEntity;
import com.iherb.herb.service.TropismService;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.R;



/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-18 12:11:29
 */
@Api(tags="归经接口")
@RestController
@RequestMapping("/herb/tropism")
@CrossOrigin
public class TropismController {
    @Autowired
    private TropismService tropismService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取tropismHerb数据列表。\n" +
            "page--第几页，从1开始\nlimit--一页多少条数据\nkey--关键词，可选参数")
    public R list(@RequestParam("page") Long curPage, @RequestParam("limit") Long limit, String key){
        PageUtils page = tropismService.queryPage(curPage, limit, key);

        return R.ok().data("page", page);
    }

    @GetMapping("listAll")
    public R listAll() {
        List<TropismEntity> list = tropismService.list();
        return R.ok().data("list", list);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的tropism数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
        TropismEntity tropism = tropismService.getById(id);

        return R.ok().data("tropism", tropism);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加tropism数据}")
    public R save(@RequestBody TropismEntity tropism){
        tropismService.save(tropism);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改tropism数据}")
    public R update(@RequestBody TropismEntity tropism){
        tropismService.updateById(tropism);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的tropism数据}")
    public R delete(@RequestBody Long[] ids){
        tropismService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
