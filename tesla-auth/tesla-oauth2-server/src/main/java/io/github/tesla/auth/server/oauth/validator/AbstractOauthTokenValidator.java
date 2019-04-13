package io.github.tesla.auth.server.oauth.validator;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.auth.server.oauth.support.OAuthTokenxRequest;

/**
 * 2015/7/4
 *
 *
 */
public abstract class AbstractOauthTokenValidator extends AbstractClientDetailsValidator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractOauthTokenValidator.class);

    protected OAuthTokenxRequest tokenRequest;

    protected AbstractOauthTokenValidator(OAuthTokenxRequest tokenRequest) {
        super(tokenRequest);
        this.tokenRequest = tokenRequest;
    }

    protected String grantType() {
        return tokenRequest.getGrantType();
    }

    protected OAuthResponse invalidGrantTypeResponse(String grantType) throws OAuthSystemException {
        return OAuthResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
            .setError(OAuthError.TokenResponse.INVALID_GRANT)
            .setErrorDescription("Invalid grant_type '" + grantType + "'").buildJSONMessage();
    }

    // true is invalided
    protected boolean invalidUsernamePassword() {
        final String username = tokenRequest.getUsername();
        final String password = tokenRequest.getPassword();
        try {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password));
        } catch (Exception e) {
            LOG.debug("Login failed by username: " + username, e);
            return true;
        }
        return false;
    }

    protected OAuthResponse invalidUsernamePasswordResponse() throws OAuthSystemException {
        return OAuthResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
            .setError(OAuthError.TokenResponse.INVALID_GRANT).setErrorDescription("Bad credentials").buildJSONMessage();
    }

}
