package com.iherb.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.iherb.common.constant.ResultConstant;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    /**
     * 失败
     */
    public static final int ERROR = 400;

    //4xx - 通用
    public static final int ARGUMENT_INVALID = 401;
    public static final int ARGUMENT_MISSING = 402;
    public static final int RESULT_NOT_FOUND = 403;
    public static final int UNKNOWN_ERROR = 404;

    //5xx - MySQL
    public static final int SQL_ERROR_GENERAL = 500;
    public static final int SQL_ERROR_INSERT = 501;
    public static final int SQL_ERROR_DELETE = 502;
    public static final int SQL_ERROR_UPDATE = 503;
    public static final int SQL_ERROR_SELECT = 504;

    //6xx - 微信
    public static final int WX_ERROR_GENERAL = 600;
    public static final int WX_ERROR_LOGIN = 601;
    public static final int WX_ERROR_UPLOAD = 602;

    private Integer code;

    private String message;

    private Map<String, Object> data;

    public static R ok() {
        R r = new R();
        r.setCode(SUCCESS);
        r.setMessage("成功");
        r.setData(new HashMap<>());
        return r;
    }

    public static R error() {
        R r = new R();
        r.setCode(ERROR);
        r.setMessage("服务器产生未知异常");
        r.setData(new HashMap<>());
        return r;
    }

    public static R error(Integer code, String message) {
        R r = new R();
        r.setCode(code);
        r.setMessage(message);
        r.setData(new HashMap<>());
        return r;
    }

    public R data(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public R data(JSONObject data) {
        this.data = data.getInnerMap();
        return this;
    }
}
