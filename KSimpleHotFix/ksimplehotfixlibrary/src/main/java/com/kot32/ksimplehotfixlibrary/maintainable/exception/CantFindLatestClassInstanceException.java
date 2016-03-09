package com.kot32.ksimplehotfixlibrary.maintainable.exception;

/**
 * @author Lody
 */
public class CantFindLatestClassInstanceException extends RuntimeException {


    public CantFindLatestClassInstanceException(String message) {
        super(message);
    }

    public CantFindLatestClassInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantFindLatestClassInstanceException() {
        super();
    }

    public CantFindLatestClassInstanceException(Throwable cause) {
        super(cause);
    }
}
