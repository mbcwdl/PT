package com.playtogether.authcenter.vo;

import lombok.Data;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/5 13:30
 */
@Data
public class LoginBody {
    /**
     * 登录方式  密码 或 验证码
     */
    private String action;
    /**
     * 可能为手机或者邮箱
     */
    private String account;
    private String password;
    private String verifyCode;
}
