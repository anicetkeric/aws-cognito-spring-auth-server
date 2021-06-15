package com.authorization.sample.awscognitospringauthserver.service.dto;

import com.authorization.sample.awscognitospringauthserver.annotation.ValidPassword;
import com.authorization.sample.awscognitospringauthserver.annotation.PasswordValueMatch;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@PasswordValueMatch.List({
        @PasswordValueMatch(
                field = "password",
                fieldMatch = "passwordConfirm",
                message = "Passwords do not match!"
        )
})
@AllArgsConstructor()
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserPasswordUpdateDTO extends AuthenticatedChallengeDTO {

    @ValidPassword
    @NonNull
    @NotBlank(message = "New password is mandatory")
    private String password;


    @ValidPassword
    @NonNull
    @NotBlank(message = "Confirm Password is mandatory")
    private String passwordConfirm;
}
