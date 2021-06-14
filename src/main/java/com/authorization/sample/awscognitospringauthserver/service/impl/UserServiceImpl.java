package com.authorization.sample.awscognitospringauthserver.service.impl;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException;
import com.authorization.sample.awscognitospringauthserver.service.CognitoUserService;
import com.authorization.sample.awscognitospringauthserver.service.UserService;
import com.authorization.sample.awscognitospringauthserver.service.dto.AuthenticatedChallengeDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.AuthenticatedResponse;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final CognitoUserService cognitoUserService;


    @Override
    public BaseResponse authenticate(LoginDTO userLogin) {

        AdminInitiateAuthResult result =  cognitoUserService.initiateAuth(userLogin.getUsername(), userLogin.getPassword())
                                                             .orElseThrow(() -> new UserNotFoundException(String.format("Username %s  not found.", userLogin.getUsername())));

        // Password change required on first login
        if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
            return new BaseResponse(AuthenticatedChallengeDTO.builder()
                            .challengeType(NEW_PASSWORD_REQUIRED.name())
                            .sessionId(result.getSession())
                            .username(userLogin.getUsername())
                            .build(), "First TimeLogin - Password change required", true);
        }

        return new BaseResponse(AuthenticatedResponse.builder()
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userLogin.getUsername())
                .build(), "Login successful", true);
    }
}
