package io.github.tesla.auth.server.oauth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.tesla.auth.server.common.Constant;
import io.github.tesla.auth.server.oauth.domian.oauth.AccessToken;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;
import io.github.tesla.auth.server.oauth.exception.*;
import io.github.tesla.auth.server.oauth.service.OAuthRSService;
import io.github.tesla.auth.server.oauth.support.OAuthHandler;
import io.github.tesla.auth.server.utils.WebUtils;

@Controller
@RequestMapping("/oauth")
public class OauthValidateController {

    private Logger logger = LoggerFactory.getLogger("oauthValidate");
    @Autowired
    private OAuthRSService rsService;

    @RequestMapping(value = "check_token", method = RequestMethod.GET)
    public void checkToken(HttpServletRequest request, HttpServletResponse response) {
        OAuthResponse oAuthResponse = null;
        try {
            String accessToken = getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                throw new OAuth2TokenEmptyException("Invalid access_token: " + accessToken);
            }
            String resourceId = getResourceId(request);
            // Validate access token
            AccessToken aToken = rsService.loadAccessTokenByTokenId(accessToken);
            validateToken(accessToken, aToken);
            // Validate client details by resource-id
            final ClientDetails clientDetails = rsService.loadClientDetails(aToken.clientId(), resourceId);
            validateClientDetails(accessToken, aToken, clientDetails);
            String username = aToken.username();
            // Null username is invalid
            if (username == null) {
                throw new OAuth2AccountEmptyException("Null usernames are not allowed by this realm.");
            }
            try {
                oAuthResponse = OAuthHandler.createTokenResponse(aToken, false, clientDetails);
            } catch (OAuthSystemException e) {
                throw new OAuth2ServerErrorException(e);
            }
        } catch (OAuth2Exception e) {
            logger.error(e.getMessage(), e);
            oAuthResponse = e.getResponse();
        }
        WebUtils.writeOAuthJsonResponse(response, oAuthResponse);
    }

    private String getAccessToken(HttpServletRequest httpRequest) {
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null) {
            // bearer ab1ade69-d122-4844-ab23-7b109ad977f0
            return authorization.substring(6).trim();
        }
        return httpRequest.getParameter(OAuth.OAUTH_ACCESS_TOKEN);
    }

    private String getResourceId(HttpServletRequest httpRequest) {
        final String resourceId = httpRequest.getHeader(Constant.resourceIdHeader);
        if (StringUtils.isNotBlank(resourceId)) {
            return resourceId;
        }
        return httpRequest.getParameter(Constant.resourceIdParam);
    }

    private void validateToken(String token, AccessToken accessToken)
        throws OAuth2TokenEmptyException, OAuth2TokenExpiredException {
        if (accessToken == null) {
            logger.debug("Invalid access_token: {}, because it is null", token);
            throw new OAuth2TokenEmptyException("Invalid access_token: " + token);
        }
        if (accessToken.tokenExpired()) {
            logger.debug("Invalid access_token: {}, because it is expired", token);
            throw new OAuth2TokenExpiredException("Invalid access_token: " + token);
        }
    }

    private void validateClientDetails(String token, AccessToken accessToken, ClientDetails clientDetails)
        throws OAuth2ClientEmptyException, OAuth2ClientArchivedException {
        if (clientDetails == null) {
            logger.debug("Invalid ClientDetails: {} by client_id: {}, it is null", clientDetails,
                accessToken.clientId());
            throw new OAuth2ClientEmptyException("Invalid client by token: " + token);
        }
        if (clientDetails.archived()) {
            logger.debug("Invalid ClientDetails: {} by client_id: {}, it is archived", clientDetails,
                accessToken.clientId());
            throw new OAuth2ClientArchivedException("Invalid client by token: " + token);
        }
    }

}