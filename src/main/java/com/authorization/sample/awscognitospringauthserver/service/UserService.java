package com.authorization.sample.awscognitospringauthserver.service;

import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserPasswordUpdateDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.AuthenticatedResponse;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;


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
}
