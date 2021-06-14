package com.authorization.sample.awscognitospringauthserver.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticatedChallengeDTO {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String username;

    @NotBlank
    private String challengeType;
}
