package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.TropismHerbDao;
import com.iherb.herb.entity.TropismHerbEntity;
import com.iherb.herb.service.TropismHerbService;


@Service("tropismHerbService")
public class TropismHerbServiceImpl extends ServiceImpl<TropismHerbDao, TropismHerbEntity> implements TropismHerbService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<TropismHerbEntity> page = this.page(
                new Query<TropismHerbEntity>().getPage(curPage, limit),
                new QueryWrapper<TropismHerbEntity>()
        );

        return new PageUtils(page);
    }

}
