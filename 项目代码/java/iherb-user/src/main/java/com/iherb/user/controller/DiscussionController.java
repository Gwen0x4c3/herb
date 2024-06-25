package com.iherb.user.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.entity.vo.DiscussionVo;
import com.iherb.user.util.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.iherb.user.entity.DiscussionEntity;
import com.iherb.user.service.DiscussionService;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.R;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
@Api("Discussion模块")
@RestController
@RequestMapping("user/discussion")
@CrossOrigin
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private WxUtil wxUtil;

    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取discussion数据列表。\n" +
            "page--第几页，从1开始\nlimit--一页多少条数据\nkey--关键词，可选参数")
    public R list(@RequestParam("page") Long page){
        PageUtils res = discussionService.getDiscussions(page);

        return R.ok().data("page", res);
    }

    @GetMapping("/{id}")
    public R getDiscussion(@PathVariable Long id, HttpServletRequest request) {
        String userId = wxUtil.getUserIdFromRequest(request);
        DiscussionVo discussion = discussionService.getDiscussion(id, userId);
        return R.ok().data("discussion", discussion);
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的discussion数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
		DiscussionEntity discussion = discussionService.getById(id);

        return R.ok().data("discussion", discussion);
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加discussion数据}")
    public R save(@RequestBody DiscussionEntity discussion, HttpServletRequest request){
        String userId = wxUtil.getUserIdFromRequest(request);
        discussionService.saveDiscussion(discussion, userId);

        return R.ok();
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改discussion数据}")
    public R update(@RequestBody DiscussionEntity discussion){
		discussionService.updateById(discussion);

        return R.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的discussion数据}")
    public R delete(@RequestBody Long[] ids){
		discussionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
