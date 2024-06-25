package com.iherb.user.dao;

import com.iherb.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author liguoen
 * @email 932713272@qq.com
 * @date 2022-08-20 17:51:57
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
	
}
