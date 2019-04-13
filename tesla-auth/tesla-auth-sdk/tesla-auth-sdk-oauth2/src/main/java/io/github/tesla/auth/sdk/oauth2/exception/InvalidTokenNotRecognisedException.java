package io.github.tesla.auth.sdk.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

public class InvalidTokenNotRecognisedException extends InvalidTokenException {

    public InvalidTokenNotRecognisedException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidTokenNotRecognisedException(String msg) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_token_not_recognised";
    }
}