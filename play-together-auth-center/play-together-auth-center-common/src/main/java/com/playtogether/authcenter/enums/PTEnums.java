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

    /**
     * 服务器内部错误
     */
    SERVER_ERROR(500, "服务器开了点小差哦");

    private final int code;
    private final String message;

}
