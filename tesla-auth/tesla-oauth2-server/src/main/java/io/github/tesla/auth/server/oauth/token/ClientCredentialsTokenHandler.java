package io.github.tesla.auth.server.oauth.token;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.support.OAuthTokenxRequest;
import io.github.tesla.auth.server.oauth.validator.AbstractClientDetailsValidator;
import io.github.tesla.auth.server.oauth.validator.ClientCredentialsClientDetailsValidator;
import io.github.tesla.auth.server.utils.WebUtils;

/**
 * 2015/7/3
 * <p/>
 * grant_type=client_credentials
 *
 *
 */
public class ClientCredentialsTokenHandler extends AbstractOAuthTokenHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsTokenHandler.class);

    @Override
    public boolean support(OAuthTokenxRequest tokenRequest) throws OAuthProblemException {
        final String grantType = tokenRequest.getGrantType();
        return GrantType.CLIENT_CREDENTIALS.toString().equalsIgnoreCase(grantType);
    }

    /**
     * /oauth/token?client_id=credentials-client&client_secret=credentials-secret&grant_type=client_credentials&scope=read
     * write
     * <p/>
     * Response access_token If exist AccessToken and it is not expired, return it otherwise, return a new AccessToken
     * <p/>
     * {"access_token":"38187f32-e4fb-4650-8e4a-99903b66f20e","token_type":"bearer","expires_in":7}
     */
    @Override
    public void handleAfterValidation() throws OAuthProblemException, OAuthSystemException {

        AccessToken accessToken =
            oauthService.retrieveClientCredentialsAccessToken(clientDetails(), tokenRequest.getScopes());
        final OAuthResponse tokenResponse = createTokenResponse(accessToken, false);

        LOG.debug("'client_credentials' response: {}", tokenResponse);
        WebUtils.writeOAuthJsonResponse(response, tokenResponse);

    }

    @Override
    protected AbstractClientDetailsValidator getValidator() {
        return new ClientCredentialsClientDetailsValidator(tokenRequest);
    }

}
