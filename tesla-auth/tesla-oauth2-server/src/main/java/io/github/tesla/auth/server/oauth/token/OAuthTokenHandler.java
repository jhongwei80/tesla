package io.github.tesla.auth.server.oauth.token;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import io.github.tesla.auth.server.oauth.support.OAuthTokenxRequest;

/**
 * 2015/7/3
 *
 *
 */

public interface OAuthTokenHandler {

    boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException;

    void handle(OAuthTokenxRequest tokenRequest, HttpServletResponse response)
        throws OAuthProblemException, OAuthSystemException;

}