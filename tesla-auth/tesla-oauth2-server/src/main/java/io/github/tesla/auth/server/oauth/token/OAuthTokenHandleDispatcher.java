package io.github.tesla.auth.server.oauth.token;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.auth.server.oauth.support.OAuthTokenxRequest;

/**
 * 2015/7/3
 *
 *
 */
public class OAuthTokenHandleDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenHandleDispatcher.class);

    private List<OAuthTokenHandler> handlers = new ArrayList<>();

    private final OAuthTokenxRequest tokenRequest;
    private final HttpServletResponse response;

    public OAuthTokenHandleDispatcher(OAuthTokenxRequest tokenRequest, HttpServletResponse response) {
        this.tokenRequest = tokenRequest;
        this.response = response;

        initialHandlers();
    }

    private void initialHandlers() {
        handlers.add(new AuthorizationCodeTokenHandler());
        handlers.add(new PasswordTokenHandler());
        handlers.add(new RefreshTokenHandler());

        handlers.add(new ClientCredentialsTokenHandler());

        LOG.debug("Initialed '{}' OAuthTokenHandler(s): {}", handlers.size(), handlers);
    }

    public void dispatch() throws OAuthProblemException, OAuthSystemException {
        for (OAuthTokenHandler handler : handlers) {
            if (handler.support(tokenRequest)) {
                LOG.debug("Found '{}' handle OAuthTokenxRequest: {}", handler, tokenRequest);
                handler.handle(tokenRequest, response);
                return;
            }
        }
        throw new IllegalStateException("Not found 'OAuthTokenHandler' to handle OAuthTokenxRequest: " + tokenRequest);
    }
}
