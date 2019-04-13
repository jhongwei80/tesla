package io.github.tesla.auth.server.oauth.support;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError;

public enum Oauth2ValidateEnum {

    success(HttpServletResponse.SC_OK, null),
    clientEmpty(HttpServletResponse.SC_UNAUTHORIZED, OAuthError.TokenResponse.INVALID_CLIENT),
    clientArchived(HttpServletResponse.SC_UNAUTHORIZED, OAuthError.TokenResponse.INVALID_CLIENT),
    tokenEmpty(HttpServletResponse.SC_UNAUTHORIZED, OAuthError.TokenResponse.INVALID_REQUEST),
    tokenExpired(HttpServletResponse.SC_UNAUTHORIZED, OAuthError.ResourceResponse.EXPIRED_TOKEN),
    serverError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, OAuthError.OAUTH_ERROR),
    accountEmpty(HttpServletResponse.SC_UNAUTHORIZED, OAuthError.TokenResponse.INVALID_REQUEST);

    private int code;
    private String tokenResponse;

    Oauth2ValidateEnum(int code, String tokenResponse) {
        this.code = code;
        this.tokenResponse = tokenResponse;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTokenResponse() {
        return tokenResponse;
    }

    public void setTokenResponse(String tokenResponse) {
        this.tokenResponse = tokenResponse;
    }
}
