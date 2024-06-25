package com.iherb.herb.controller;

import java.util.Arrays;
import java.util.List;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iherb.herb.entity.FunctionHerbEntity;
import com.iherb.herb.service.FunctionHerbService;
import com.iherb.herb.entity.vo.FunctionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.iherb.herb.entity.FunctionEntity;
import com.iherb.herb.service.FunctionService;
import com.iherb.common.utils.R;



/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
@Api(tags="功效接口")
@RestController
@RequestMapping("/herb/function")
@CrossOrigin
public class FunctionController {
    @Autowired
    private FunctionService functionService;
    @Autowired
    private FunctionHerbService functionHerbService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取function数据列表。")
    public R list(){
        List<FunctionVo> functionVos = functionService.listWithTree();

        return R.ok().data("list", functionVos);
    }

    @GetMapping("/listWithData")
    @ApiOperation(value = "获取数据列表", notes = "获取function数据列表。")
    public R listWithData(){
        List<FunctionVo> functionVos = functionService.listWithTree();

        return R.ok().data("list", functionVos);
    }

    @GetMapping("/listParents")
    @ApiOperation(value = "获取父级分类", notes = "获取function父级列表。")
    public R listParents(){
        List<FunctionEntity> list = functionService.list(new QueryWrapper<FunctionEntity>()
                .eq("parent_id", 0));
        return R.ok().data("list", list);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的function数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
        FunctionEntity function = functionService.getById(id);

        return R.ok().data("function", function);
    }

    @PostMapping("/relate")
    public R relate(@RequestBody FunctionHerbEntity functionHerb) {
        functionHerbService.save(functionHerb);

        return R.ok();
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加function数据}")
    public R save(@RequestBody FunctionEntity function){
        functionService.save(function);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改function数据}")
    public R update(@RequestBody FunctionEntity function){
        functionService.updateById(function);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的function数据}")
    public R delete(@RequestBody Long[] ids){
        functionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
