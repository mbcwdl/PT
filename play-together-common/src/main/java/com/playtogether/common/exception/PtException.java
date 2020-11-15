package com.playtogether.common.exception;

import com.playtogether.common.enums.PtCommonEnums;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 13:52
 */
@Data
@AllArgsConstructor
public class PtException extends RuntimeException{
    private int code;
    private String message;

    public PtException(PtCommonEnums enums) {
        this.code = enums.getCode();
        this.message = enums.getMessage();
    }

}
