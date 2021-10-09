package com.authorization.sample.awscognitospringauthserver.exception;

import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {


    @ExceptionHandler({FailedAuthenticationException.class, NotAuthorizedException.class, com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException.class, InvalidPasswordException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse unauthorizedExceptions(Exception ex) {
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return new BaseResponse(null, ex.getMessage());

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new BaseResponse(errors, "Validation failed");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, UsernameExistsException.class, InvalidParameterException.class})
    public BaseResponse processValidationError(ConstraintViolationException ex) {
        return new BaseResponse(null, ex.getMessage());
    }


    @ExceptionHandler({Exception.class, InternalErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse handleAllExceptions(Exception ex) {
        //log ex.getLocalizedMessage()
        ex.printStackTrace();
        log.error(ex.getMessage(), ex.getLocalizedMessage());
        return new BaseResponse(null, (ex.getMessage() != null) ? ex.getMessage() : "Oops something went wrong !!!");

    }
}
