package com.playtogether.common.exception;

import com.playtogether.common.enums.PTCommonEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 13:52
 */
@Data
@AllArgsConstructor
public class PTException extends RuntimeException{
    private int code;
    private String message;

    public PTException(PTCommonEnums enums) {
        this.code = enums.getCode();
        this.message = enums.getMessage();
    }

}
