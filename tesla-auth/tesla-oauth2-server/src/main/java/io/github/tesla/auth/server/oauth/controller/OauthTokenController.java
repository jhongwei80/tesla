package io.github.tesla.auth.server.oauth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.tesla.auth.server.oauth.support.OAuthTokenxRequest;
import io.github.tesla.auth.server.oauth.token.OAuthTokenHandleDispatcher;
import io.github.tesla.auth.server.utils.WebUtils;

@Controller
@RequestMapping("oauth")
public class OauthTokenController {

    /**
     * Handle grant_types as follows:
     * <p/>
     * grant_type=authorization_code grant_type=password grant_type=refresh_token grant_type=client_credentials
     *
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @throws OAuthSystemException
     */
    @RequestMapping("token")
    public void authorize(HttpServletRequest request, HttpServletResponse response) throws OAuthSystemException {
        try {
            OAuthTokenxRequest tokenRequest = new OAuthTokenxRequest(request);

            OAuthTokenHandleDispatcher tokenHandleDispatcher = new OAuthTokenHandleDispatcher(tokenRequest, response);
            tokenHandleDispatcher.dispatch();

        } catch (OAuthProblemException e) {
            // exception
            OAuthResponse oAuthResponse = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                .location(e.getRedirectUri()).error(e).buildJSONMessage();
            WebUtils.writeOAuthJsonResponse(response, oAuthResponse);
        }

    }
}
