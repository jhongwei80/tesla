package io.github.tesla.auth.sdk.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class CustomAccessTokenConverterException extends OAuth2Exception {
    public CustomAccessTokenConverterException(String msg) {
        super(msg);
    }
}