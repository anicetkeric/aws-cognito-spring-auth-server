package com.authorization.sample.awscognitospringauthserver.service;

import com.authorization.sample.awscognitospringauthserver.service.dto.LoginDTO;
import com.authorization.sample.awscognitospringauthserver.web.response.BaseResponse;


public interface UserService {

    /**
     * @param userLogin user login infos
     * @return BaseResponse
     */
    BaseResponse authenticate(LoginDTO userLogin);
}
