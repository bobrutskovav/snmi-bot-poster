package ru.aleksx.articleprovider.exception;

public class UnsuccessfulArticleSendException extends RuntimeException {
    public UnsuccessfulArticleSendException() {
    }

    public UnsuccessfulArticleSendException(String message) {
        super(message);
    }

    public UnsuccessfulArticleSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsuccessfulArticleSendException(Throwable cause) {
        super(cause);
    }

    public UnsuccessfulArticleSendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
