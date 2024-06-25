package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.FunctionHerbDao;
import com.iherb.herb.entity.FunctionHerbEntity;
import com.iherb.herb.service.FunctionHerbService;


@Service("functionHerbService")
public class FunctionHerbServiceImpl extends ServiceImpl<FunctionHerbDao, FunctionHerbEntity> implements FunctionHerbService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<FunctionHerbEntity> page = this.page(
                new Query<FunctionHerbEntity>().getPage(curPage, limit),
                new QueryWrapper<FunctionHerbEntity>()
        );

        return new PageUtils(page);
    }

}
