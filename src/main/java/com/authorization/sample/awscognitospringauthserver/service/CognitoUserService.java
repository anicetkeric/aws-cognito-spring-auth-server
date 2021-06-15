package com.authorization.sample.awscognitospringauthserver.service;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;

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
     * @param username
     * @param newPassword
     * @param session
     * @return
     */
    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session);
}
