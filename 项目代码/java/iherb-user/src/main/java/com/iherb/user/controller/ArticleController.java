package com.iherb.user.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.iherb.user.entity.es.Article;
import com.iherb.user.entity.vo.ArticleVo;
import com.iherb.user.util.KeywordUtil;
import com.iherb.user.util.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import com.iherb.user.entity.ArticleEntity;
import com.iherb.user.service.ArticleService;
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
@Api("Article模块")
@RestController
@RequestMapping("user/article")
@CrossOrigin
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private KeywordUtil keywordUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WxUtil wxUtil;

    @GetMapping("/list")
    @ApiOperation(value = "获取数据列表", notes = "获取article数据列表。\n" +
            "page--第几页，从1开始\nlimit--一页多少条数据\nkey--关键词，可选参数")
    public R list(@RequestParam("page") Long curPage, @RequestParam("limit") Long limit, String key){
        PageUtils page = articleService.queryPage(curPage, limit, key);

        return R.ok().data("page", page);
    }

    @GetMapping("recommend")
    public R recommendArticle(Long[] excludeIds, HttpServletRequest request) {
        String userId = wxUtil.getUserIdFromRequest(request);
        List<Article> list = articleService.recommend(userId, excludeIds);
        return R.ok().data("list", list);
    }

    @GetMapping("/{id}")
    public R getArticle(@PathVariable Long id, HttpServletRequest request) {
        String userId = wxUtil.getUserIdFromRequest(request);
        ArticleVo article = articleService.getArticle(id, userId);
        return R.ok().data("article", article);
    }

    @GetMapping("/info/{id}")
    @ApiOperation(value = "获取指定id的数据", notes = "获取指定id的article数据。\n" +
            "id--数据的id，使用url传参")
    public R info(@PathVariable("id") Long id){
		ArticleEntity article = articleService.getById(id);

        return R.ok().data("article", article);
    }

    @PostMapping("/save")
    @ApiOperation(value = "添加数据", notes = "添加article数据}")
    public R save(@RequestBody ArticleEntity article, HttpServletRequest request){
        String userId = wxUtil.getUserIdFromRequest(request);
        articleService.saveArticle(article, userId);

        return R.ok();
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改数据", notes = "根据上传数据中id修改article数据}")
    public R update(@RequestBody ArticleEntity article){
		articleService.updateById(article);

        return R.ok();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除数据", notes = "删除id在所传数组中的article数据}")
    public R delete(@RequestBody Long[] ids){
		articleService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
