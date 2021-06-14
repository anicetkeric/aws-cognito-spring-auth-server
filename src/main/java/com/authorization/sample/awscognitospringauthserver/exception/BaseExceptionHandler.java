package com.authorization.sample.awscognitospringauthserver.exception;

import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {


    @ExceptionHandler({FailedAuthenticationException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse unauthorizedExceptions(Exception ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return new BaseResponse(null, ex.getMessage());

    }



}
