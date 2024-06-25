package com.iherb.herb.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iherb.herb.entity.HerbEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iherb.herb.entity.vo.HerbFunctionVo;
import com.iherb.herb.entity.vo.HerbTropismVo;
import com.iherb.herb.entity.vo.HerbVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-06-14 12:39:14
 */
@Mapper
public interface HerbDao extends BaseMapper<HerbEntity> {

    Page<HerbFunctionVo> queryPageByFunctionParentId(Long id, Page<HerbFunctionVo> page);

    Page<HerbFunctionVo> queryPageByFunctionId(Long id, Page<HerbFunctionVo> page);

    List<HerbVo> queryAllWithText();

    List<HerbFunctionVo> listWithFunction(@Param("functionId") Long functionId);

    List<HerbTropismVo> listWithTropism(@Param("tropismId") Long tropismId);
}
