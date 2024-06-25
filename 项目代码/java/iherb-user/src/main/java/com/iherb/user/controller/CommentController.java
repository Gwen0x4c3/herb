package com.iherb.user.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.iherb.user.entity.CommentEntity;
import com.iherb.user.service.CommentService;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.R;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:56
 */
@Api("Comment模块")
@RestController
@RequestMapping("user/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取comment数据列表。\n" +
            "page--第几页，从1开始\nlimit--一页多少条数据\nkey--关键词，可选参数")
    public R list(Long page, Long id, Integer type, @RequestParam(name="sort",defaultValue="0") Integer sort){
        PageUtils res = commentService.listComments(id, type, page, sort);

        return R.ok().data("page", res);
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的comment数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
		CommentEntity comment = commentService.getById(id);

        return R.ok().data("comment", comment);
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加comment数据}")
    public R save(@RequestBody CommentEntity comment, HttpServletRequest request){
        String ticket = request.getHeader("TICKET");
        String userId = (String) redisTemplate.opsForValue().get("WX_LOGIN_" + ticket);
        comment.setUserId(userId);
        comment.setCreateTime(new Date());
        System.out.println(comment);
        commentService.addComment(comment);

        return R.ok();
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改comment数据}")
    public R update(@RequestBody CommentEntity comment){
		commentService.updateById(comment);

        return R.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的comment数据}")
    public R delete(@RequestBody Long[] ids){
		commentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
