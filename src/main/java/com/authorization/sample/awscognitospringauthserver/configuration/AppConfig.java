package com.authorization.sample.awscognitospringauthserver.configuration;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppConfig {

    private final AwsConfig awsConfig;


    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProviderClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsConfig.getAccessKey(), awsConfig.getSecretKey())))
                .withRegion(awsConfig.getRegion())
                .build();
    }


}
