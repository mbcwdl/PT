package com.playtogether.auth.exception;

import com.playtogether.auth.enums.PtEnums;
import com.playtogether.common.exception.PtException;

public class PtAuthException extends PtException {

    public PtAuthException(PtEnums enums) {
        super(enums.getCode(), enums.getMessage());
    }
}
