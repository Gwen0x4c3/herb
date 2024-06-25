package com.iherb.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iherb.user.entity.vo.CommentVo;
import com.iherb.user.entity.vo.DiscussionVo;
import com.iherb.user.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.user.dao.CommentDao;
import com.iherb.user.entity.CommentEntity;
import com.iherb.user.service.CommentService;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    public static final int ROWS = 10;

    @Autowired
    private ArticleService articleService;

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(curPage, limit),
                new QueryWrapper<CommentEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void addComment(CommentEntity comment) {
        this.save(comment);
        articleService.incrCommentCount(comment.getTargetId());
    }

    @Override
    public PageUtils listComments(Long id, Integer type, Long page, Integer sort) {
        Page<CommentVo> myPage = this.baseMapper.listComments(id, type, sort, new Page<CommentVo>(page, ROWS));
        List<CommentVo> records = myPage.getRecords();
        long total = myPage.getTotal();
        System.out.println(myPage.getPages());
        if (sort == 0) {
            for (int i = 0; i < records.size(); i++) {
                records.get(i).setFloor((int) (ROWS * (page - 1) + i + 1));
            }
        } else {
            for (int i = 0; i < records.size(); i++) {
                records.get(i).setFloor((int) (total - i - (page - 1) * ROWS));
            }
        }
        return new PageUtils(myPage);
    }

}
