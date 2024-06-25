package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.TropismDao;
import com.iherb.herb.entity.TropismEntity;
import com.iherb.herb.service.TropismService;


@Service("tropismService")
public class TropismServiceImpl extends ServiceImpl<TropismDao, TropismEntity> implements TropismService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<TropismEntity> page = this.page(
                new Query<TropismEntity>().getPage(curPage, limit),
                new QueryWrapper<TropismEntity>()
        );

        return new PageUtils(page);
    }

}
