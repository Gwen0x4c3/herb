package com.iherb.herb.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.SymptomDao;
import com.iherb.herb.entity.SymptomEntity;
import com.iherb.herb.service.SymptomService;


@Service("symptomService")
public class SymptomServiceImpl extends ServiceImpl<SymptomDao, SymptomEntity> implements SymptomService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<SymptomEntity> page = this.page(
                new Query<SymptomEntity>().getPage(curPage, limit),
                new QueryWrapper<SymptomEntity>()
        );

        return new PageUtils(page);
    }

}
