package com.playtogether.usercenter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/4 16:33
 */
@Getter
@AllArgsConstructor
public enum  PTEnums {

    SEVER_ERROR(500, "服务器开了点小差哦"),
    NICKNAME_ALREADY_EXIST(400, "昵称已存在"),
    NICKNAME_CANNOT_BE_NULL(400, "昵称不能为空"),
    NICKNAME_LENGTH_ILLEGAL(400, "昵称为6-12个字符"),
    PHONE_ALREADY_EXIST(400, "手机号已被注册"),
    PHONE_NOT_REGISTER(400, "该手机号未注册"),
    PHONE_CANNOT_BE_NULL(400, "手机号不能为空"),
    PHONE_PATTERN_ILLEGAL(400, "手机号格式错误"),
    PHONE_OR_PASSWORD_ERROR(400, "手机号或密码错误"),
    SEND_INTERVAL_LESS_THAN_ONE_MINUTE(403, "验证码发送间隔小于1分钟"),
    SEND_MAX_TIME_MORE_THAN_CONTROL(403, "每天最多发送10条短信"),
    PASSWORD_CANNOT_BE_NULL(400, "密码不能为空");
    private final int code;
    private final String message;

}