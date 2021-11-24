package com.github.cbyzzy.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * Author:cbyzzy
 */
@ApiModel(description = "返回对象封装")
public class ResultEntity<T> {

    @ApiModelProperty(value = "状态码", example = "200")
    private int code;

    @ApiModelProperty(value = "响应数据,默认success")
    private T data;

    @ApiModelProperty(value = "发生异常显示信息", example = "用户名密码错误")
    private String message = "";

    @ApiModelProperty(value = "请求url", example = "/login")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String url;

    @ApiModelProperty(hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MyError error;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return code != 200 && message == null ? code + " Server Error" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MyError getError() {
        return error;
    }

    public void setError(MyError error) {
        this.error = error;
    }

    public static class MyError {
        private String message;

        private Throwable throwable;

        public MyError(String message) {
            this.message = message;
        }

        public MyError(String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }
}
