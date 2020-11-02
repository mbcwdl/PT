package com.playtogether.common.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/** 返回结果对象
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 13:40
 */
@Data
public class R implements Serializable {
    private int code;
    private Boolean success;
    private String message;
    private Object data;

    public static R ok () {
        R r = new R();
        r.setCode(200);
        r.setSuccess(true);
        r.setMessage("接口访问成功");
        return r;
    }

    public static R fail () {
        R r = new R();
        r.setSuccess(false);
        return r;
    }

    public R code (int code) {
        this.setCode(code);
        return this;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R data(Object data) {
        this.setData(data);
        return this;
    }
}
