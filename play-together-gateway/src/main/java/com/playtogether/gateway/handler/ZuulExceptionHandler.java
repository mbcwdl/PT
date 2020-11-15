package com.playtogether.gateway.handler;

import com.netflix.zuul.exception.ZuulException;
import com.playtogether.common.vo.R;
import com.playtogether.gateway.exception.PtZuulException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/14 11:32
 */
@RestController
public class ZuulExceptionHandler {

    @RequestMapping(
            value = "/error",
            method = { GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE },
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public R error(HttpServletRequest request) {
        ZuulException e = (ZuulException) request.getAttribute("javax.servlet.error.exception");
        R result;
        if (e instanceof PtZuulException) {
            PtZuulException ptZuulException = (PtZuulException) e;
            result = R.fail()
                    .message(ptZuulException.getMessage())
                    .code(ptZuulException.getCode());
        } else {
            result = R.fail().message(e.getMessage()).code(500);
        }
        return result;
    }
}
