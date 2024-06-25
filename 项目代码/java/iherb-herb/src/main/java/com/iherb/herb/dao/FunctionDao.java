package com.iherb.herb.dao;

import com.iherb.herb.entity.FunctionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
@Mapper
public interface FunctionDao extends BaseMapper<FunctionEntity> {

    List<FunctionEntity> searchFunctions(Long[] ids);
}
