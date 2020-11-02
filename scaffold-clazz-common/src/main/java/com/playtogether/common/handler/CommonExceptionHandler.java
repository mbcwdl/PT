package com.playtogether.common.handler;

import com.playtogether.common.exception.PTException;
import com.playtogether.common.vo.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 通用异常处理器
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 13:49
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler({Exception.class})
    public R exception (Exception e) {
        return R.fail().message("服务器开了点小差哦").code(500);
    }

    @ExceptionHandler(PTException.class)
    public R ptException (PTException e) {
        return R.fail().code(e.getCode()).message(e.getMessage());
    }

}
