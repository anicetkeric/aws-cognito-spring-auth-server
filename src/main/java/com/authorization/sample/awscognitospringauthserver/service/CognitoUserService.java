package com.authorization.sample.awscognitospringauthserver.service;

import com.amazonaws.services.cognitoidp.model.*;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserSignUpDTO;

import java.util.Optional;

public interface CognitoUserService {

    /**
     * Authenticate Cognito User
     *
     * @param username user name
     * @param password user password
     * @return Optional<AdminInitiateAuthResult>
     */
    Optional<AdminInitiateAuthResult> initiateAuth(String username, String password);


    /**
     * @param username    username
     * @param newPassword new user password
     * @param session     user session di
     * @return Optional AdminRespondToAuthChallengeResult
     */
    Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session);


    /**
     * Signs out users from all devices.
     *
     * @param accessToken access token
     * @return GlobalSignOutResult
     */
    GlobalSignOutResult signOut(String accessToken);

    /**
     * Send forgot password flow
     *
     * @param username username
     * @return {@link ForgotPasswordResult}
     */
    ForgotPasswordResult forgotPassword(String username);


    /**
     * Add a group to user
     *
     * @param username  user name
     * @param groupName group name
     */
    void addUserToGroup(String username, String groupName);


    /**
     * set permanent password to make user status as CONFIRMED
     *
     * @param username username
     * @param password user password
     * @return AdminSetUserPasswordResult
     */
    AdminSetUserPasswordResult setUserPassword(String username, String password);

    /**
     * Creates a new user in the specified user pool.
     *
     * @param signUpDTO user info
     * @return UserType
     */
    UserType signUp(UserSignUpDTO signUpDTO);


    /**
     * @param username  username username
     * @param maxResult The maximum number of authentication events to return.
     * @param nextToken A pagination token.
     * @return AdminListUserAuthEventsResult
     */
    AdminListUserAuthEventsResult getUserAuthEvents(String username, int maxResult, String nextToken);
}
