package com.playtogether.authcenter.exception;

import com.playtogether.authcenter.enums.PTEnums;
import com.playtogether.common.exception.PTException;

public class PTAuthException extends PTException {

    public PTAuthException(PTEnums enums) {
        super(enums.getCode(), enums.getMessage());
    }
}
