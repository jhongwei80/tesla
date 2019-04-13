package io.github.tesla.auth.sdk.oauth2;

import java.io.OutputStream;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

public class BkjkOAuthApi extends DefaultApi20 {

    private String endpoint;

    BkjkOAuthApi(String endpoint) {
        this.endpoint = endpoint;
    }

    public static BkjkOAuthApi instance(String endpoint) {
        return new BkjkOAuthApi(endpoint);
    }

    @Override
    public String getAccessTokenEndpoint() {
        return endpoint + "/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return endpoint + "/oauth/authorize";
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OAuth2AccessTokenJsonExtractor.instance();
    }

    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String scope,
        OutputStream debugStream, String state, String responseType, String userAgent,
        HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new BkjkOAuthServiceProvider(this, apiKey, apiSecret, callback, scope, state, responseType, userAgent,
            httpClientConfig, httpClient);
    }
}