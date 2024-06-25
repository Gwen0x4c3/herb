package com.iherb.herb.service.impl;

import com.iherb.herb.entity.vo.FunctionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.herb.dao.FunctionDao;
import com.iherb.herb.entity.FunctionEntity;
import com.iherb.herb.service.FunctionService;


@Service("functionService")
public class FunctionServiceImpl extends ServiceImpl<FunctionDao, FunctionEntity> implements FunctionService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<FunctionEntity> page = this.page(
                new Query<FunctionEntity>().getPage(curPage, limit),
                new QueryWrapper<FunctionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<FunctionEntity> searchFunctions(Long[] ids) {
        return this.baseMapper.searchFunctions(ids);
    }

    @Override
    public List<FunctionVo> listWithTree() {
        List<FunctionEntity> list = this.list();
        List<FunctionVo> vos = list.stream().filter(f -> f.getParentId() == 0).map(f -> {
            FunctionVo vo = new FunctionVo();
            BeanUtils.copyProperties(f, vo);
            vo.setChildren(findChildren(vo.getId(), list));
            return vo;
        }).collect(Collectors.toList());

        return vos;
    }

    @Override
    public FunctionVo listWithTree(Long functionId) {
        List<FunctionEntity> list = this.list(new QueryWrapper<FunctionEntity>()
                .eq("id", functionId)
                .or()
                .eq("parent_id", functionId));
        List<FunctionVo> vos = list.stream().filter(f -> f.getParentId() == 0).map(f -> {
            FunctionVo vo = new FunctionVo();
            BeanUtils.copyProperties(f, vo);
            vo.setChildren(findChildren(vo.getId(), list));
            return vo;
        }).collect(Collectors.toList());

        return vos.get(0);
    }

    private List<FunctionVo> findChildren(Long id, List<FunctionEntity> source) {
        return source.stream().filter(f -> f.getParentId().equals(id)).map(f -> {
            FunctionVo vo = new FunctionVo();
            BeanUtils.copyProperties(f, vo);
            return vo;
        }).collect(Collectors.toList());
    }

}
