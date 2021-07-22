package com.authorization.sample.awscognitospringauthserver.service.dto;

import com.authorization.sample.awscognitospringauthserver.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class UserSignUpDTO {

    @NotBlank
    @NotNull
    @Email
    private String email;

    @ValidPassword
    private String password;

    @NotBlank
    @NotNull
    private String name;

    private String nationality;

    private String lastname;

    private String phoneNumber;

    @NotNull
    @NotEmpty
    private Set<String> roles;

}
