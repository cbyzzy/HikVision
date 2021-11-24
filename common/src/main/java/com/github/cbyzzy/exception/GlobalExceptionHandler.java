package com.github.cbyzzy.exception;

import com.github.cbyzzy.entity.ResultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 自定义返回值和异常处理
 * Author:cbyzzy
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 自定义异常处理
     * @param e WebException
     * @return resultEntity
     */
    @ExceptionHandler(value = WebException.class)
    public ResultEntity webErrorHandler(HttpServletRequest request, WebException e) {
        logger.error(e.getMessage(), e);
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resultEntity.setMessage(e.getMessage());
        resultEntity.setUrl(request.getRequestURI());
        resultEntity.setError(new ResultEntity.MyError(e.getError()));
        return resultEntity;
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResultEntity authErrorHandler(HttpServletRequest request, UnAuthorizedException e) {
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(HttpStatus.UNAUTHORIZED.value());
        resultEntity.setMessage(e.getMessage());
        resultEntity.setUrl(request.getRequestURI());
        resultEntity.setError(null);
        return resultEntity;
    }

    /**
     * 统一异常处理
     * @param e Exception
     * @return ResultEntity
     */
    @ExceptionHandler(value = Exception.class)
    public ResultEntity defaultErrorHandler(HttpServletRequest request, Exception e) {
        logger.error(e.getMessage(), e);
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        resultEntity.setMessage(e.getMessage());
        resultEntity.setUrl(request.getRequestURI());
        resultEntity.setError(new ResultEntity.MyError(e.getMessage(), e));
        return resultEntity;
    }

}
