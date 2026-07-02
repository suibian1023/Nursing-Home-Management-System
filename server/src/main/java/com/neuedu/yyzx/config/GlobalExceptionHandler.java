package com.neuedu.yyzx.config;

import com.neuedu.yyzx.utils.ResultVo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResultVo<?> handleExpiredJwt(ExpiredJwtException e) {
        return ResultVo.fail("token已过期，请重新登录");
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResultVo<?> handleInvalidJwt(Exception e) {
        return ResultVo.fail("token无效");
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception e) {
        e.printStackTrace();
        return ResultVo.fail("服务器内部错误: " + e.getMessage());
    }
}
