package com.authorization.sample.awscognitospringauthserver.exception;

@SuppressWarnings("serial")
public class UsernameExistsException extends ServiceException {
    public UsernameExistsException() {
        super();
    }

    public UsernameExistsException(String message) {
        super(message);
    }

    public UsernameExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
