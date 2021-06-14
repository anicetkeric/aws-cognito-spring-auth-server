package com.authorization.sample.awscognitospringauthserver.common;

import org.springframework.beans.BeanWrapperImpl;
import com.authorization.sample.awscognitospringauthserver.annotation.PasswordValueMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <h2>PasswordFieldsValueMatchValidator</h2>
 *
 * @author aek
 */
public class PasswordFieldsValueMatchValidator implements ConstraintValidator<PasswordValueMatch, Object> {

    private String field;
    private String fieldMatch;
    private String message;

    public void initialize(PasswordValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
        this.message = constraintAnnotation.message();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(fieldMatch);

        boolean isValid = false;
        if (fieldValue != null) {
            isValid = fieldValue.equals(fieldMatchValue);
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(fieldMatch)
                    .addConstraintViolation();
        }

        return isValid;

    }

}
