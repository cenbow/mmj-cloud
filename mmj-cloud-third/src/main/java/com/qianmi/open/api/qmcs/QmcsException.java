package com.qianmi.open.api.qmcs;

/**
 * 消息异常
 */
public class QmcsException extends Exception {

    private static final long serialVersionUID = 1980323882616514221L;

    private int errorCode;

    public int getErrorCode() {
        return this.errorCode;
    }

    public QmcsException() {
        this("");
    }

    public QmcsException(String message) {
        super(message);
    }

    public QmcsException(String message, Exception innerException) {
        super(message, innerException);
    }

    public QmcsException(String message, Throwable innerException) {
        super(message, innerException);
    }

    public QmcsException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public QmcsException(int errorCode, String message, Exception innerException) {
        super(message, innerException);
        this.errorCode = errorCode;
    }

    public QmcsException(int errorCode, String message, Throwable innerException) {
        super(message, innerException);
        this.errorCode = errorCode;
    }
}
