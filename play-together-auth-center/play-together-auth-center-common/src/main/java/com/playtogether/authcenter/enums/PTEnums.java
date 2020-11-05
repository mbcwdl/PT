package com.playtogether.authcenter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 15:04
 */
@Getter
@AllArgsConstructor
public enum PTEnums {

    SEND_INTERVAL_LESS_THAN_ONE_MINUTE(403, "验证码发送间隔小于1分钟"),
    SEND_MAX_TIME_MORE_THAN_CONTROL(403, "每天最多发送10条短信"),
    VERIFY_CODE_CANNOT_BE_NULL(400, "请先发送验证码"),
    PHONE_PATTERN_ILLEGAL(400, "手机号格式错误"),
    PHONE_CANNOT_BE_NULL(400, "手机号不能为空"),
    PHONE_OR_VERIFY_CODE_ERROR(400, "手机号或验证码错误"),
    ACCOUNT_OR_PASSWORD_ERROR(400, "账号或密码错误");

    private final int code;
    private final String message;

}
