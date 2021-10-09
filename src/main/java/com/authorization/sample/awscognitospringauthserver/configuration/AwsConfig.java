package com.authorization.sample.awscognitospringauthserver.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific to aws client.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Data
@Component
@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
public class AwsConfig {

    /**
     * Aws access key ID
     */
    private String accessKey;


    /**
     * Aws secret access key
     */
    private String secretKey;

    /**
     * Aws region
     */
    private String region;

    /**
     * Aws Cognito client properties
     */
    private final Cognito cognito = new Cognito();


    @Getter
    @Setter
    public static class Cognito {
        private String userPoolId;
        private String appClientId;
        private String appClientSecret;
    }


}
