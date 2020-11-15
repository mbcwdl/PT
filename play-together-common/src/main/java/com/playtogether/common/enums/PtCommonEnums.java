package com.playtogether.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/5 9:27
 */
@Getter
@AllArgsConstructor
public enum PtCommonEnums {

    BAD_REQUEST(400, "参数错误"),
    INVALID_REQUEST(404, "无效请求"),
    SERVER_ERROR(500, "服务器开了点小差哦");

    private final int code;
    private final String message;
}
