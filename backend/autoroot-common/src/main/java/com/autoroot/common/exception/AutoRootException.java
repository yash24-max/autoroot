package com.autoroot.common.exception;

/**
 * Base exception for AutoRoot application.
 */
public class AutoRootException extends RuntimeException {

    private final String errorCode;

    public AutoRootException(String message) {
        super(message);
        this.errorCode = "AUTOROOT_ERROR";
    }

    public AutoRootException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AutoRootException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AUTOROOT_ERROR";
    }

    public AutoRootException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}