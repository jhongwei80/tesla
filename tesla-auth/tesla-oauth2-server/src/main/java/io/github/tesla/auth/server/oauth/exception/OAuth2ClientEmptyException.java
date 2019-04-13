package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public class OAuth2ClientEmptyException extends OAuth2Exception {

    public OAuth2ClientEmptyException() {}

    public OAuth2ClientEmptyException(String message) {
        super(message);
    }

    public OAuth2ClientEmptyException(Throwable cause) {
        super(cause);
    }

    public OAuth2ClientEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public OAuthResponse getResponse() {
        return invalidClientErrorResponse(Oauth2ValidateEnum.clientEmpty, "client is empty");
    }
}
