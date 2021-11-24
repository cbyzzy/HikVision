package com.github.cbyzzy.exception;

/**
 * Description:
 * Author: cbyzzy
 */
public class UnAuthorizedException extends RuntimeException {

    private static final long serialVersionUID = -2468775338549111163L;

    private String error;

    public UnAuthorizedException() {
        this("没有访问权限，请先登录");
    }

    public UnAuthorizedException(String error) {
        super(error);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
