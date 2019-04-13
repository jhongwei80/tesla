package io.github.tesla.ops.common;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 * 
 */
@RestControllerAdvice
public class TeslaExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AuthorizationException.class)
    public CommonResponse handleAuthorizationException(AuthorizationException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("未授权");
    }

    @ExceptionHandler(TeslaException.class)
    public CommonResponse handleBDException(TeslaException e) {
        logger.error("处理异常", e);
        CommonResponse r = new CommonResponse();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());
        return r;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public CommonResponse handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("数据库中已存在该记录");
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public CommonResponse handlerIncorrectCredentialsException(IncorrectCredentialsException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("用户名密码错误");
    }

    @ExceptionHandler(LockedAccountException.class)
    public CommonResponse handlerLockedAccountException(LockedAccountException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("用户被锁，请联系管理员");
    }

    @ExceptionHandler(UnknownAccountException.class)
    public CommonResponse handlerUnknownAccountException(UnknownAccountException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("用户不存在");
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public CommonResponse noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
        logger.error(e.getMessage(), e);
        return CommonResponse.error("没找找到页面");
    }

}
