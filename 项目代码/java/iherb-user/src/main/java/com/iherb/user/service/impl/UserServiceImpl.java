package com.iherb.user.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iherb.common.utils.PageUtils;
import com.iherb.common.utils.Query;

import com.iherb.user.dao.UserDao;
import com.iherb.user.entity.UserEntity;
import com.iherb.user.service.UserService;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Long curPage, Long limit, String key) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(curPage, limit),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

}
