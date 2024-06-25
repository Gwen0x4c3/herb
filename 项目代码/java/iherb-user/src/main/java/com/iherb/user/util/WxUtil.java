package com.iherb.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class WxUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public String getUserIdFromRequest(HttpServletRequest request) {
        String ticket = request.getHeader("TICKET");
        String userId = (String) redisTemplate.opsForValue().get("WX_LOGIN_" + ticket);
        return userId;
    }
}
