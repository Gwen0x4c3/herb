package com.iherb.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iherb.common.utils.MD5;
import com.iherb.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("wx")
@CrossOrigin
public class UserController {

    private static final String APP_ID = "wx7de8eec11b611fb7";
    private static final String APP_SECRET = "7dd9ee2a8f1e360ace131e5962e2d93c";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("login")
    public R login(@RequestParam("code") String code, String userTicket) {
        if (StringUtils.isNotBlank(userTicket)) {
            if (redisTemplate.hasKey("WX_LOGIN_" + userTicket)) {
                return R.ok();
            }
        }
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APP_ID +
                "&secret=" + APP_SECRET +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        JSONObject resData = JSON.parseObject(restTemplate.getForObject(url, String.class));
        System.out.println(resData);
        if (resData.containsKey("errcode")) {
            return R.error(R.WX_ERROR_LOGIN, "登录失败");
        }
        String openId = resData.getString("openid");
        String ticket = MD5.encrypt(openId);
        redisTemplate.opsForValue().set("WX_LOGIN_" + ticket, openId, 30, TimeUnit.DAYS);
        return R.ok().data("ticket", ticket);
    }

}
