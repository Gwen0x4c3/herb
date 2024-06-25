package com.iherb.user.controller;

import com.iherb.common.utils.R;
import com.iherb.user.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@CrossOrigin
public class AdminController {

    @PostMapping("login")
    public R login(@RequestBody User user) {
        System.out.println(user);
        return R.ok().data("token", "llaslsatoken");
    }

    @GetMapping("info")
    public R info(String token) {
        System.out.println(token);
        return R.ok()
                .data("roles", "student")
                .data("name", "老毕等")
                .data("avatar", "https://p6.itc.cn/images01/20210401/0bb30b1ad5d74581a9a945daf7c098dc.jpeg");
    }

}
