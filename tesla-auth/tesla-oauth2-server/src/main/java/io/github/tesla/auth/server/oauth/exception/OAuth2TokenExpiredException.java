package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public class OAuth2TokenExpiredException extends OAuth2Exception {

    public OAuth2TokenExpiredException() {}

    public OAuth2TokenExpiredException(String message) {
        super(message);
    }

    public OAuth2TokenExpiredException(Throwable cause) {
        super(cause);
    }

    public OAuth2TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public OAuthResponse getResponse() {
        return invalidClientErrorResponse(Oauth2ValidateEnum.tokenExpired, "token is expired");
    }
}
