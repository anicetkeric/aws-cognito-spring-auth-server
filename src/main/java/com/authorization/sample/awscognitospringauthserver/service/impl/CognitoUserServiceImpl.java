package com.authorization.sample.awscognitospringauthserver.service.impl;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.authorization.sample.awscognitospringauthserver.configuration.AwsConfig;
import com.authorization.sample.awscognitospringauthserver.domain.enums.CognitoAttributesEnum;
import com.authorization.sample.awscognitospringauthserver.exception.FailedAuthenticationException;
import com.authorization.sample.awscognitospringauthserver.exception.ServiceException;
import com.authorization.sample.awscognitospringauthserver.service.CognitoUserService;
import com.authorization.sample.awscognitospringauthserver.service.dto.UserSignUpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public UserType signUp(UserSignUpDTO signUpDTO){

        try {
            final AdminCreateUserRequest signUpRequest = new AdminCreateUserRequest()
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    // The user's temporary password.
                    .withTemporaryPassword(generateValidPassword())
                    // Specify "EMAIL" if email will be used to send the welcome message
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withUsername(signUpDTO.getEmail())
                    .withMessageAction(MessageActionType.SUPPRESS)
                    .withUserAttributes(
                            new AttributeType().withName("name").withValue(signUpDTO.getName()),
                            new AttributeType().withName("family_name").withValue(signUpDTO.getLastname()),
                            new AttributeType().withName("custom:nationality").withValue(signUpDTO.getNationality()),
                            new AttributeType().withName("email").withValue(signUpDTO.getEmail()),
                            new AttributeType().withName("email_verified").withValue("true"),
                            new AttributeType().withName("phone_number").withValue(signUpDTO.getPhoneNumber()),
                            new AttributeType().withName("phone_number_verified").withValue("true"));

            // create user
            AdminCreateUserResult createUserResult =  awsCognitoIdentityProvider.adminCreateUser(signUpRequest);
            log.info("Created User id: {}", createUserResult.getUser().getUsername());

            // assign the roles
            signUpDTO.getRoles().forEach(r -> addUserToGroup(signUpDTO.getEmail(), r));

            // set permanent password
            setUserPassword(signUpDTO.getEmail(), signUpDTO.getPassword());

            return createUserResult.getUser();

        } catch (com.amazonaws.services.cognitoidp.model.UsernameExistsException e) {
            throw new UsernameExistsException("User name that already exists");
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new com.authorization.sample.awscognitospringauthserver.exception.InvalidPasswordException("Invalid password.", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
   public void addUserToGroup(String username, String groupName){

        try {
            // add user to group
            AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest()
                    .withGroupName(groupName)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withUsername(username);

            awsCognitoIdentityProvider.adminAddUserToGroup(addUserToGroupRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new FailedAuthenticationException(String.format("Invalid parameter: %s", e.getErrorMessage()), e);
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AdminSetUserPasswordResult setUserPassword(String username, String password){

        try {
            // Sets the specified user's password in a user pool as an administrator. Works on any user.
            AdminSetUserPasswordRequest adminSetUserPasswordRequest = new AdminSetUserPasswordRequest()
                    .withUsername(username)
                    .withPassword(password)
                    .withUserPoolId(awsConfig.getCognito().getUserPoolId())
                    .withPermanent(true);

            return awsCognitoIdentityProvider.adminSetUserPassword(adminSetUserPasswordRequest);
        } catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
            throw new FailedAuthenticationException(String.format("Invalid parameter: %s", e.getErrorMessage()), e);
        }
    }


    /**
     * {@inheritDoc}
     */
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
    /**
     * {@inheritDoc}
     */
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
    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalSignOutResult signOut(String accessToken) {
        try {
            return awsCognitoIdentityProvider.globalSignOut(new GlobalSignOutRequest().withAccessToken(accessToken));
        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Logout failed: %s", e.getErrorMessage()), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ForgotPasswordResult forgotPassword(String username) {
        try {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.withClientId(awsConfig.getCognito().getAppClientId())
                    .withUsername(username)
                    .withSecretHash(calculateSecretHash(awsConfig.getCognito().getAppClientId(), awsConfig.getCognito().getAppClientSecret(),username));

           return awsCognitoIdentityProvider.forgotPassword(request);

        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Forgot password failed: %s", e.getErrorMessage()), e);
        }
    }

    private Optional<AdminInitiateAuthResult> adminInitiateAuthResult(AdminInitiateAuthRequest request) {
        try {
            return Optional.of(awsCognitoIdentityProvider.adminInitiateAuth(request));
        } catch (NotAuthorizedException e) {
            throw new FailedAuthenticationException(String.format("Authenticate failed: %s", e.getErrorMessage()), e);
        } catch (UserNotFoundException e) {
            String username = request.getAuthParameters().get(CognitoAttributesEnum.USERNAME.name());
            throw new com.authorization.sample.awscognitospringauthserver.exception.UserNotFoundException(String.format("Username %s  not found.", username), e);
        }
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
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


    /**
     * @return password generated
     */
    private String generateValidPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERRONEOUS_SPECIAL_CHARS";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

}
