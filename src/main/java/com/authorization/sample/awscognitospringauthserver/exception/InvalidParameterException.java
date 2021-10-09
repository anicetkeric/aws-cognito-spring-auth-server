package com.authorization.sample.awscognitospringauthserver.exception;

@SuppressWarnings("serial")
public class InvalidParameterException extends ServiceException {
    public InvalidParameterException() {
        super();
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
