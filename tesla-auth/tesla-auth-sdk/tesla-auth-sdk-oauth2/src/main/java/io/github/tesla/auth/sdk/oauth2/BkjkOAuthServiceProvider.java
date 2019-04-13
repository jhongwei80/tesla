package io.github.tesla.auth.sdk.oauth2;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;

public class BkjkOAuthServiceProvider extends OAuth20Service {

    private String apiKey;
    private String apiSecret;
    private String callback;
    private String scope;
    private String state;

    protected BkjkOAuthServiceProvider(DefaultApi20 api, String apiKey, String apiSecret, String callback, String scope,
        String state, String responseType, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, scope, state, responseType, userAgent, httpClientConfig, httpClient);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.callback = callback;
        this.scope = scope;
        this.state = state;
    }

    @Override
    protected OAuthRequest createAccessTokenRequest(String code) {
        final OAuthRequest request = new OAuthRequest(getApi().getAccessTokenVerb(), getApi().getAccessTokenEndpoint());
        request.addParameter(OAuthConstants.CLIENT_ID, apiKey);
        if (apiSecret != null) {
            request.addParameter(OAuthConstants.CLIENT_SECRET, apiSecret);
        }
        request.addParameter(OAuthConstants.CODE, code);
        request.addParameter(OAuthConstants.REDIRECT_URI, callback);
        if (scope != null) {
            request.addParameter(OAuthConstants.SCOPE, scope);
        }
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
        request.addHeader(OAuthConstants.HEADER, OAuthConstants.BASIC + ' ' + Base64.getEncoder()
            .encodeToString(String.format("%s:%s", apiKey, apiSecret).getBytes(Charset.forName("UTF-8"))));
        return request;
    }

    @Override
    public String getAuthorizationUrl(Map<String, String> params) {
        return super.getAuthorizationUrl() + "&login_type=" + params.get("login_type") //
            + "&partner_key=" + params.get("partner_key") //
            + "&display=" + params.get("display");
    }
}