package io.github.tesla.auth.server.oauth.exception;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.auth.server.oauth.support.Oauth2ValidateEnum;

public abstract class OAuth2Exception extends Exception {

    private Logger logger = LoggerFactory.getLogger(OAuth2Exception.class);

    public OAuth2Exception() {}

    public OAuth2Exception(String message) {
        super(message);
    }

    public OAuth2Exception(Throwable cause) {
        super(cause);
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract OAuthResponse getResponse();

    protected OAuthResponse invalidClientErrorResponse(Oauth2ValidateEnum validateEnum, String errorDescription) {
        try {
            return OAuthResponse.errorResponse(validateEnum.getCode()).setError(validateEnum.getTokenResponse())
                .setErrorDescription(errorDescription).buildJSONMessage();
        } catch (OAuthSystemException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
