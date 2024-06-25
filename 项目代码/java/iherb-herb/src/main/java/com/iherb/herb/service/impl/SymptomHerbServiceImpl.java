package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.SymptomHerbDao;
import com.iherb.herb.entity.SymptomHerbEntity;
import com.iherb.herb.service.SymptomHerbService;


@Service("symptomHerbService")
public class SymptomHerbServiceImpl extends ServiceImpl<SymptomHerbDao, SymptomHerbEntity> implements SymptomHerbService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<SymptomHerbEntity> page = this.page(
                new Query<SymptomHerbEntity>().getPage(curPage, limit),
                new QueryWrapper<SymptomHerbEntity>()
        );

        return new PageUtils(page);
    }

}
