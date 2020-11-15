package com.playtogether.common.handler;

import com.playtogether.common.exception.PtException;
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
        e.printStackTrace();
        return R.fail().message(e.getClass().getName() + ":" + e.getMessage()).code(500);
    }

    @ExceptionHandler(PtException.class)
    public R ptException (PtException e) {
        e.printStackTrace();
        return R.fail().code(e.getCode()).message(e.getMessage());
    }

}
