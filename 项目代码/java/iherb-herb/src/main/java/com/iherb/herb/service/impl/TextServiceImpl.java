package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.TextDao;
import com.iherb.herb.entity.TextEntity;
import com.iherb.herb.service.TextService;


@Service("textService")
public class TextServiceImpl extends ServiceImpl<TextDao, TextEntity> implements TextService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<TextEntity> page = this.page(
                new Query<TextEntity>().getPage(curPage, limit),
                new QueryWrapper<TextEntity>()
        );

        return new PageUtils(page);
    }

}
