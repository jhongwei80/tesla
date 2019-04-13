package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public class OAuth2ServerErrorException extends OAuth2Exception {

    public OAuth2ServerErrorException() {}

    public OAuth2ServerErrorException(String message) {
        super(message);
    }

    public OAuth2ServerErrorException(Throwable cause) {
        super(cause);
    }

    public OAuth2ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public OAuthResponse getResponse() {
        return invalidClientErrorResponse(Oauth2ValidateEnum.serverError, "server error");
    }
}
