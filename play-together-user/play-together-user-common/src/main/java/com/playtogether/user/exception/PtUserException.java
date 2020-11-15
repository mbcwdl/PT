package com.playtogether.user.exception;

import com.playtogether.common.exception.PtException;
import com.playtogether.user.enums.PtEnums;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/4 17:05
 */
public class PtUserException extends PtException {

    public PtUserException(PtEnums enums) {
        super(enums.getCode(), enums.getMessage());
    }
}
