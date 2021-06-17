package com.authorization.sample.awscognitospringauthserver.service;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.GlobalSignOutResult;

import java.util.Optional;

public interface CognitoUserService {

    /**
     * Authenticate Cognito User
     * @param username user name
     * @param password user password
     * @return Optional<AdminInitiateAuthResult>
     */
    Optional<AdminInitiateAuthResult> initiateAuth(String username, String password);


    /**
     * @param username username
     * @param newPassword new user password
     * @param session user session di
     * @return Optional AdminRespondToAuthChallengeResult
     */
    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session);


    /**
     * Signs out users from all devices.
     * @param accessToken access token
     * @return GlobalSignOutResult
     */
    GlobalSignOutResult signOut(String accessToken);

    /**
     * Send forgot password flow
     * @param username username
     * @return @{@link ForgotPasswordResult}
     */
    ForgotPasswordResult forgotPassword(String username);
}
