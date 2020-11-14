package com.playtogether.gateway.exception;

import com.netflix.zuul.exception.ZuulException;
import lombok.Getter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 14:40
 */

@Getter
public class PTZuulException extends ZuulException {
    private int code;

    public PTZuulException(int code, String message) {
        super(message, code, message);
        this.code = code;
    }
}
