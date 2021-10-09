package com.authorization.sample.awscognitospringauthserver.service.impl;

import com.amazonaws.services.cognitoidp.model.*;
import com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException;
import com.authorization.sample.awscognitospringauthserver.service.CognitoUserService;
import com.authorization.sample.awscognitospringauthserver.service.UserService;
import com.authorization.sample.awscognitospringauthserver.service.dto.AuthenticatedChallengeDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserPasswordUpdateDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserSignUpDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.AuthenticatedResponse;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.NotNull;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final CognitoUserService cognitoUserService;


    /**
     * {@inheritDoc}
     */
    @Override
    public BaseResponse authenticate(LoginDTO userLogin) {

        AdminInitiateAuthResult result = cognitoUserService.initiateAuth(userLogin.getUsername(), userLogin.getPassword())
                .orElseThrow(() -> new UserNotFoundException(String.format("Username %s  not found.", userLogin.getUsername())));

        // Password change required on first login
        if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
            return new BaseResponse(AuthenticatedChallengeDTO.builder()
                    .challengeType(NEW_PASSWORD_REQUIRED.name())
                    .sessionId(result.getSession())
                    .username(userLogin.getUsername())
                    .build(), "First time login - Password change required", false);
        }

        return new BaseResponse(AuthenticatedResponse.builder()
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userLogin.getUsername())
                .build(), "Login successful", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthenticatedResponse updateUserPassword(UserPasswordUpdateDTO userPassword) {

        AdminRespondToAuthChallengeResult result =
                cognitoUserService.respondToAuthChallenge(userPassword.getUsername(), userPassword.getPassword(), userPassword.getSessionId()).get();

        return AuthenticatedResponse.builder()
                .accessToken(result.getAuthenticationResult().getAccessToken())
                .idToken(result.getAuthenticationResult().getIdToken())
                .refreshToken(result.getAuthenticationResult().getRefreshToken())
                .username(userPassword.getUsername())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(@NotNull String accessToken) {
        cognitoUserService.signOut(accessToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ForgotPasswordResult userForgotPassword(String username) {
        return cognitoUserService.forgotPassword(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserType createUser(UserSignUpDTO signUpDTO) {
        return cognitoUserService.signUp(signUpDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken) {
        return cognitoUserService.getUserAuthEvents(username, maxResult, nextToken);
    }
}
