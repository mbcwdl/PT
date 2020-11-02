package com.playtogether.common.exception;

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
}
