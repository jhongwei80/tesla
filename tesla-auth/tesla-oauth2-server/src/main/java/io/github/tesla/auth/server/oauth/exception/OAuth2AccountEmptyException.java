package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public class OAuth2AccountEmptyException extends OAuth2Exception {

    public OAuth2AccountEmptyException() {}

    public OAuth2AccountEmptyException(String message) {
        super(message);
    }

    public OAuth2AccountEmptyException(Throwable cause) {
        super(cause);
    }

    public OAuth2AccountEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public OAuthResponse getResponse() {
        return invalidClientErrorResponse(Oauth2ValidateEnum.accountEmpty, "account is empty");
    }
}
