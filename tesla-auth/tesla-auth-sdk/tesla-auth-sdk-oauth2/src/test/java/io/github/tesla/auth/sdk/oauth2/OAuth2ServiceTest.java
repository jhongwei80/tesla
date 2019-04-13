package io.github.tesla.auth.sdk.oauth2;

import com.github.scribejava.core.builder.ServiceBuilder;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: wangzhiguo
 * @Date: 2018/11/27 14:02
 */
public class OAuth2ServiceTest {
    private static final String CLIENT_ID = "client";
    private static final String CLIENT_SECRET = "secret";
    private static final String SCOPE = "app test";
    private static final String STATE = "secret-rensanning";
    private static final String callbackUrl = "https://oauth-resourece.dev.bkjk.com/api/profile";
    private static final String endpoint = "https://api.dev.bkjk.com";

    @Ignore
    @Test
    public void authorizeUrl() {
        OAuth20Service oAuth20Service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .scope(SCOPE)
                .state(STATE)
                .callback(callbackUrl)
                .build(BkjkOAuthApi.instance(endpoint));

        Map<String, String> params = new HashMap<>();
        params.put("login_type", "0");
        params.put("partner_key", "BKJK");
        String url = oAuth20Service.getAuthorizationUrl(params);
        System.out.println(url);
    }

    @Ignore
    @Test(expected = Exception.class)
    public void accessTokenForAuthorizationCode() throws InterruptedException, ExecutionException, IOException {
        OAuth20Service oAuth20Service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .scope(SCOPE)
                .state(STATE)
                .callback(callbackUrl)
                .build(BkjkOAuthApi.instance(endpoint));
        String code = "d54wFE";
        OAuth2AccessToken oAuth2AccessToken = oAuth20Service.getAccessToken(code);
        if (null != oAuth2AccessToken) {
            System.out.println("accessToken:" + oAuth2AccessToken.getAccessToken());
            System.out.println("expiresIn:" + oAuth2AccessToken.getExpiresIn());
            System.out.println("refreshToken:" + oAuth2AccessToken.getRefreshToken());
            System.out.println("refreshTokenExpiresIn:" + oAuth2AccessToken.getRefreshTokenExpiresIn());
        }
    }

    @Ignore
    @Test
    public void refreshToken() throws InterruptedException, ExecutionException, IOException {
        OAuth20Service oAuth20Service = new ServiceBuilder("BKJK")
                .apiSecret("bkjksecret")
                .scope(SCOPE)
                .state(STATE)
                .callback(callbackUrl)
                .build(BkjkOAuthApi.instance(endpoint));
        String refreshToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyZWZyZXNoX3Rva2VuX2V4cGlyZXNfaW4iOjMxMTAzOTk5LCJhdWQi"
                + "Olsib2F1dGgyLXJlc291cmNlIl0sImFkZGl0aW9uYWxfaW5mbyI6InI1QXVBSXplMWdNOHVlTzZVUm96cDRxZFFiZ0E0VHVRbElTUXdMN1NIY"
                + "XJUK1NrYTF3U3NEL3FjMnV3bk9IUDY4Y3VER0VlOGovc3ZtcDNmTDF4aWNFN3JXZ29wQ1JFTWJEYXhVUWR3QUYySHF5ZnZzbTkwUm5jbnRQVF"
                + "Nyd2VCc1p3R21uRS9NT0U9IiwidXNlcl9uYW1lIjoiNTkiLCJzY29wZSI6WyJhcHAiLCJ0ZXN0Iiwic2tlcHRpY2FsIl0sImF0aSI6IjVjZGY"
                + "2OWFmLTBjMzYtNDI0NC1iNmQ2LTk3MjY3OTU0OTg4YSIsImV4cCI6MTU2NTE1NzQ0NCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0"
                + "aSI6ImUwZGU0YTY1LTNmN2ItNDk1OC1iNWQ2LTc3Y2UyYjk4MDU3ZCIsImNsaWVudF9pZCI6IkJLSksifQ.SFnHWIZfR7v5srwLQzMPnWdKm2"
                + "JZ9SmiT_E45ddL4HYhfUtNEWzruxmCBh16RMsAjrV-JgFjwjdF8dO8EsqUhPvDfFxAmOzzSgIXGzb7ZWanMocWyukfK3XsiArdZgeDEHDFHF6"
                + "tSqgdiGFVV0LFNaNduAPoOEhxn5VOTj4MGsJAz5UDcy_2KNwL1ZW1FvxhRxwa8cTPge8RyXFyzpGz8k_Rjqoa9CB4LJlEaDkgWp616QhFhxrv"
                + "U1ziOn0cFWRD60BSk_8n0Wo85_LRyVV4TXr-muZaiWhf-FCTUbjBEltd5YxW0erRjVWmyCQUN6qcxLvi72l8VUFATe23xUm7ug";
        OAuth2AccessToken oAuth2AccessToken = oAuth20Service.refreshAccessToken(refreshToken);
        if (null != oAuth2AccessToken) {
            System.out.println("accessToken:" + oAuth2AccessToken.getAccessToken());
            System.out.println("expiresIn:" + oAuth2AccessToken.getExpiresIn());
            System.out.println("refreshToken:" + oAuth2AccessToken.getRefreshToken());
            System.out.println("refreshTokenExpiresIn:" + oAuth2AccessToken.getRefreshTokenExpiresIn());
        }
    }

    @Ignore
    @Test
    public void accessTokenForClientCredentials() throws Exception {
        OAuth20Service oAuth20Service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .build(BkjkOAuthApi.instance(endpoint));

        OAuth2AccessToken oAuth2AccessToken = oAuth20Service.getAccessTokenClientCredentialsGrant();
        if (null != oAuth2AccessToken) {
            System.out.println(oAuth2AccessToken.getAccessToken());
        }
    }
}
