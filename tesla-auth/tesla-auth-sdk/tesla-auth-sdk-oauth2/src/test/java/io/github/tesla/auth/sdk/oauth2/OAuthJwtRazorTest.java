package io.github.tesla.auth.sdk.oauth2;


import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: wangzhiguo
 * @Date: 2018/11/26 19:59
 */
public class OAuthJwtRazorTest {
    Set<String> OAUTH_JWT_IGNORE_CLAIMS_KEY = ImmutableSet.of( "aud", "user_name", //
            "scope", "active", "exp", "authorities", "jti", "client_id", "refresh_token_expires_in");

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    @Ignore
    @Test
    public void claimRebuild() {
        Map<String, Object> map = new HashMap<>();
        map.put("ids", "4TTNsdFB790=");
        String claims = OAuthJwtRazor.claimRebuild(map, OAUTH_JWT_IGNORE_CLAIMS_KEY);
        System.out.println(claims);
        Assert.assertEquals("ids:646", claims);
    }

    @Ignore
    @Test(expected = Exception.class)
    public void findClaims2Token() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"
                + ".eyJyZWZyZXNoX3Rva2VuX2V4cGlyZXNfaW4iOjg2Mzk5LCJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sIm9wZW5faWQiOiI0VFROc2RGQjc5MD0iLCJtZXJjaGFudF9pZHMiOiJLeWlhSncwWlNvbz0iLCJ1c2VyX25hbWUiOiI2NDYiLCJzY29wZSI6WyJhcHAiXSwiZXhwIjoxNTMzMTk1MTUwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYWYxODNmMzAtMzQ2Ny00ZTc4LTk0OWItMmZmYTQ0OTc2YjA3IiwiY2xpZW50X2lkIjoiY2xpZW50In0"
                + ".IGqQuh_3udrPLH69M2_dNRfLQJoMIkZuVbifEv1AlzcNFymA7ZBd0jslV6_5ivfAd1vGqikC613QpC65qLGklj2MZSb8yzdRXq96nfIpdgoEVVcg2KrBbbuq2v2cAf9wo-uv0bTaosJ6vqoCvC1UPLLLMewq86EjNF_Mrc1Bom1wIDZ9Wv58gG5LRyJ0KteWhtiTWhh7AXUF6Fg9EHBMFCCTqZP89UvnF4BATbqXjC8L3OX22iUqhF9tcmqEACQcyatdIjpvtaNcyVAtNpiLRD75lpNS9voOUYa_s2ZjC4oDvRgYV2v8lNEvmY2AUNDM1QZW-TeqfWT2gcf_U7MdJQ";
        OAuthJwtRazor.findClaims2Token(token);
    }

    @Ignore
    @Test(
            //expected = Exception.class
    )
    public void checkToken() {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9"
                + ".eyJyZWZyZXNoX3Rva2VuX2V4cGlyZXNfaW4iOjg2Mzk5LCJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sIm9wZW5faWQiOiI0VFROc2RGQjc5MD0iLCJtZXJjaGFudF9pZHMiOiJLeWlhSncwWlNvbz0iLCJ1c2VyX25hbWUiOiI2NDYiLCJzY29wZSI6WyJhcHAiXSwiZXhwIjoxNTMzMTk1MTUwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiYWYxODNmMzAtMzQ2Ny00ZTc4LTk0OWItMmZmYTQ0OTc2YjA3IiwiY2xpZW50X2lkIjoiY2xpZW50In0"
                + ".IGqQuh_3udrPLH69M2_dNRfLQJoMIkZuVbifEv1AlzcNFymA7ZBd0jslV6_5ivfAd1vGqikC613QpC65qLGklj2MZSb8yzdRXq96nfIpdgoEVVcg2KrBbbuq2v2cAf9wo-uv0bTaosJ6vqoCvC1UPLLLMewq86EjNF_Mrc1Bom1wIDZ9Wv58gG5LRyJ0KteWhtiTWhh7AXUF6Fg9EHBMFCCTqZP89UvnF4BATbqXjC8L3OX22iUqhF9tcmqEACQcyatdIjpvtaNcyVAtNpiLRD75lpNS9voOUYa_s2ZjC4oDvRgYV2v8lNEvmY2AUNDM1QZW-TeqfWT2gcf_U7MdJQ";
        OAuthJwtRazor.checkToken(token);
    }
}