package com.authorization.sample.awscognitospringauthserver.service.impl;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.authorization.sample.awscognitospringauthserver.configuration.AwsConfig;
import com.authorization.sample.awscognitospringauthserver.domain.enums.CognitoAttributesEnum;
import com.authorization.sample.awscognitospringauthserver.exception.FailedAuthenticationException;
import com.authorization.sample.awscognitospringauthserver.exception.ServiceException;
import com.authorization.sample.awscognitospringauthserver.service.CognitoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@RequiredArgsConstructor
@Slf4j
@Service
public class CognitoUserServiceImpl implements CognitoUserService {

    private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;

    private final AwsConfig awsConfig;


    @Override
    public Optional<AdminInitiateAuthResult> initiateAuth(String username, String password) {

        final Map<String, String> authParams = new HashMap<>();
        authParams.put(CognitoAttributesEnum.USERNAME.name(), username);
        authParams.put(CognitoAttributesEnum.PASSWORD.name(), password);
        authParams.put(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(),username));


        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withAuthParameters(authParams);

        return adminInitiateAuthResult(authRequest);
    }

    @Override
    public Optional<AdminRespondToAuthChallengeResult> respondToAuthChallenge(
            String username, String newPassword, String session) {
        AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
        request.withChallengeName(NEW_PASSWORD_REQUIRED)
                .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                .withClientId(awsConfig.getCognito().getAppClientId())
                .withSession(session)
                .addChallengeResponsesEntry("userAttributes.name", "aek")
                .addChallengeResponsesEntry(CognitoAttributesEnum.USERNAME.name(), username)
                .addChallengeResponsesEntry(CognitoAttributesEnum.NEW_PASSWORD.name(), newPassword)
                .addChallengeResponsesEntry(CognitoAttributesEnum.SECRET_HASH.name(), calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(),username));

        try {
            return Optional.of(awsCognitoIdentityProvider.adminRespondToAuthChallenge(request));
        } catch (NotAuthorizedException e) {
            throw new NotAuthorizedException("User not found."+ e.getErrorMessage());
        } catch (UserNotFoundException e) {
            throw new com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException("User not found.", e);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new com.authorization.sample.awscognitospringauthserver.exception.InvalidPasswordException("Invalid password.", e);
        }
    }


    private Optional<AdminInitiateAuthResult> adminInitiateAuthResult(AdminInitiateAuthRequest request) {
        try {
            return Optional.of(awsCognitoIdentityProvider.adminInitiateAuth(request));
        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Authenticate failed: %s", e.getErrorMessage()), e);
        } catch (UserNotFoundException e) {
            String username = request.getAuthParameters().get("USERNAME");
            throw new com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException(String.format("Username %s  not found.", username), e);
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) throws RuntimeException {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new ServiceException("Error while calculating ");
        }
    }


}
