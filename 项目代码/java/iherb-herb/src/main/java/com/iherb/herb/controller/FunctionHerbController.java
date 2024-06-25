package com.iherb.herb.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.iherb.herb.entity.FunctionHerbEntity;
import com.iherb.herb.service.FunctionHerbService;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.R;



/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-18 12:11:29
 */
@Api("功效-中药联系接口}")
@RestController
@RequestMapping("/herb/functionherb")
@CrossOrigin
public class FunctionHerbController {
    @Autowired
    private FunctionHerbService functionHerbService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取functionHerb数据列表。\n" +
            "page--第几页，从1开始\nlimit--一页多少条数据\nkey--关键词，可选参数")
    public R list(@RequestParam("page") Long curPage, @RequestParam("limit") Long limit, String key){
        PageUtils page = functionHerbService.queryPage(curPage, limit, key);

        return R.ok().data("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的functionHerb数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
        FunctionHerbEntity functionHerb = functionHerbService.getById(id);

        return R.ok().data("functionHerb", functionHerb);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加functionHerb数据}")
    public R save(@RequestBody FunctionHerbEntity functionHerb){
        functionHerbService.save(functionHerb);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改functionHerb数据}")
    public R update(@RequestBody FunctionHerbEntity functionHerb){
        functionHerbService.updateById(functionHerb);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的functionHerb数据}")
    public R delete(@RequestBody Long[] ids){
        functionHerbService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
