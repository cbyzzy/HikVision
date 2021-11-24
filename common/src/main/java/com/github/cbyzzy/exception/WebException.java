package com.github.cbyzzy.exception;

/**
 * Description:
 * Author:cbyzzy
 */
public class WebException extends RuntimeException {

    private static final long serialVersionUID = 2771587260580067612L;

    private String error;

    public WebException(String message) {
        super(message);
    }

    public WebException(String message, String error) {
        super(message);
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
