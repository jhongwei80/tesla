package io.github.tesla.auth.sdk.oauth2;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;

import io.github.tesla.auth.common.support.DESTools;
import io.github.tesla.auth.sdk.jwt.interfaces.Claim;
import io.github.tesla.auth.sdk.oauth2.exception.CustomAccessTokenConverterException;
import io.github.tesla.auth.sdk.oauth2.exception.InvalidTokenEmptyException;
import io.github.tesla.auth.sdk.oauth2.exception.InvalidTokenExpiredException;
import io.github.tesla.auth.sdk.oauth2.exception.InvalidTokenNotRecognisedException;

public class OAuthJwtRazor {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OAuthJwtRazor.class);
    private static final AccessTokenConverter ACCESS_TOKEN_CONVERTER = accessTokenConverter();
    private static final ResourceServerTokenServices RESOURCE_SERVER_TOKEN_SERVICES = tokenServices();
    private static final String REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";
    private static final Set<String> OAUTH_JWT_IGNORE_CLAIMS_KEY = ImmutableSet.of("aud", "user_name", //
        "scope", "active", "exp", "authorities", "jti", "client_id", REFRESH_TOKEN_EXPIRES_IN, "partner_key");

    private static DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private static TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    private static CustomAccessTokenConverter customAccessTokenConverter() {
        return new CustomAccessTokenConverter();
    }

    private static JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("public.txt");
        String publicKey = null;
        try {
            publicKey = new String(ByteStreams.toByteArray(resource.getInputStream()));
        } catch (final IOException e) {
            throw new CustomAccessTokenConverterException(e.getMessage());
        }
        converter.setVerifierKey(publicKey);
        // set verifier
        converter.setVerifier(new RsaVerifier(publicKey));
        converter.setAccessTokenConverter(customAccessTokenConverter());

        return converter;
    }

    private static <T> String decrypt(T value) throws Exception {
        String decryptValue = null;
        if (value instanceof Claim) {
            decryptValue = DESTools.decrypt(((Claim)value).asString());
        } else if (value instanceof String) {
            decryptValue = DESTools.decrypt((String)value);
        }
        return decryptValue;
    }

    public static <T> String claimRebuild(Map<String, T> map, Set<String> set) {
        return Joiner.on(";").join(map.entrySet().stream().filter(entry -> !set.contains(entry.getKey())).map(entry -> {
            try {
                return entry.getKey() + ":" + decrypt(entry.getValue());
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            return null;
        }).collect(Collectors.toList()));
    }

    public static String findClaims2Token(String value) throws InvalidTokenException {
        Map<String, ?> contentMap = checkToken(value);
        return OAuthJwtRazor.claimRebuild(contentMap, OAUTH_JWT_IGNORE_CLAIMS_KEY);
    }

    /**
     * similar to call GET /oauth/check_token
     *
     * @param value
     * @return
     */
    public static Map<String, Object> checkToken(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new InvalidTokenEmptyException("param value is empty");
        }

        org.springframework.security.oauth2.common.OAuth2AccessToken token = RESOURCE_SERVER_TOKEN_SERVICES. //
            readAccessToken(value);
        if (token == null) {
            throw new InvalidTokenNotRecognisedException("Token was not recognised");
        }

        if (token.isExpired()) {
            throw new InvalidTokenExpiredException("Token has expired");
        }

        OAuth2Authentication authentication = RESOURCE_SERVER_TOKEN_SERVICES.loadAuthentication(token.getValue());
        Map<String, Object> response =
            (Map<String, Object>)ACCESS_TOKEN_CONVERTER.convertAccessToken(token, authentication);

        // gh-1070
        response.put("active", true); // Always true if token exists and not expired
        // Add by tao.yang @Since 2018/08/27
        response.remove(REFRESH_TOKEN_EXPIRES_IN);
        return response;
    }
}