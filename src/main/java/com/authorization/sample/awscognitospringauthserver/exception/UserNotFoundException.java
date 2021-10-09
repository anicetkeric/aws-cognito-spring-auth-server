package com.authorization.sample.awscognitospringauthserver.exception;

@SuppressWarnings("serial")
public class UserNotFoundException extends ServiceException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
