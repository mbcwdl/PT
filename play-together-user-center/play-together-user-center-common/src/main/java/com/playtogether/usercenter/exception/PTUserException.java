package com.playtogether.usercenter.exception;

import com.playtogether.common.exception.PTException;
import com.playtogether.usercenter.enums.PTEnums;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/4 17:05
 */
public class PTUserException extends PTException {

    public PTUserException(PTEnums enums) {
        super(enums.getCode(), enums.getMessage());
    }
}
