package io.github.tesla.auth.sdk.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

public class InvalidTokenExpiredException extends InvalidTokenException {

    public InvalidTokenExpiredException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidTokenExpiredException(String msg) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_token_expired";
    }
}