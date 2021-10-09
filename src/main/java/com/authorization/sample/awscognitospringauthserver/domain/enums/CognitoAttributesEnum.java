package com.authorization.sample.awscognitospringauthserver.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum CognitoAttributesEnum {

    USERNAME("USERNAME"),
    PASSWORD("PASSWORD"),
    SECRET_HASH("SECRET_HASH"),
    NEW_PASSWORD("NEW_PASSWORD");

    private final String values;

    CognitoAttributesEnum(String val) {
        this.values = val;

    }


    public String getValues() {
        return values;
    }

    //Lookup table
    private static final Map<String, CognitoAttributesEnum> lookup = new HashMap<>();

    //Populate the lookup
    static {
        for (CognitoAttributesEnum env : CognitoAttributesEnum.values()) {
            lookup.put(env.getValues(), env);
        }
    }

    //This method can be used for reverse lookup purpose
    public static CognitoAttributesEnum get(String key) {
        return lookup.get(key);
    }


}
