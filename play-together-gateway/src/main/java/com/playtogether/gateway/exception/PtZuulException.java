package com.playtogether.gateway.exception;

import com.netflix.zuul.exception.ZuulException;
import lombok.Getter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 14:40
 */

@Getter
public class PtZuulException extends ZuulException {
    private int code;

    public PtZuulException(int code, String message) {
        super(message, 200, message);
        this.code = code;
    }
}
