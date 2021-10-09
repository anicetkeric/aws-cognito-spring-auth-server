package com.authorization.sample.awscognitospringauthserver.service;

import com.amazonaws.services.cognitoidp.model.AdminListUserAuthEventsResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserPasswordUpdateDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserSignUpDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.AuthenticatedResponse;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;

import javax.validation.constraints.NotNull;


public interface UserService {

    /**
     * @param userLogin user login infos
     * @return BaseResponse
     */
    BaseResponse authenticate(LoginDTO userLogin);


    /**
     * @param userPasswordUpdateDTO user password DTO request
     * @return AuthenticatedResponse
     */
    AuthenticatedResponse updateUserPassword(UserPasswordUpdateDTO userPasswordUpdateDTO);


    /**
     * @param accessToken user authenticate access token
     */
    void logout(@NotNull String accessToken);


    /**
     * @param username username
     * @return {@link ForgotPasswordResult}
     */
    ForgotPasswordResult userForgotPassword(String username);

    /**
     * Creates a new user in the specified user pool.
     *
     * @param signUpDTO user info
     * @return UserType
     */
    UserType createUser(UserSignUpDTO signUpDTO);

    /**
     * @param username  username username
     * @param maxResult The maximum number of authentication events to return.
     * @param nextToken A pagination token.
     * @return AdminListUserAuthEventsResult
     */
    AdminListUserAuthEventsResult userAuthEvents(String username, int maxResult, String nextToken);
}
