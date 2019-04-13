package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public class OAuth2ClientArchivedException extends OAuth2Exception {

    public OAuth2ClientArchivedException() {}

    public OAuth2ClientArchivedException(String message) {
        super(message);
    }

    public OAuth2ClientArchivedException(Throwable cause) {
        super(cause);
    }

    public OAuth2ClientArchivedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public OAuthResponse getResponse() {
        return invalidClientErrorResponse(Oauth2ValidateEnum.clientArchived, "client is archived");
    }
}
