package io.github.tesla.filter.endpoint.plugin.request.token;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import io.github.tesla.auth.common.support.DESTools;
import io.github.tesla.auth.sdk.jwt.JWT;
import io.github.tesla.auth.sdk.jwt.JWTCreator;
import io.github.tesla.auth.sdk.jwt.algorithms.Algorithm;
import io.github.tesla.filter.endpoint.annotation.CreateTokenType;
import io.github.tesla.filter.endpoint.definition.CreateJwtTokenDefinition;
import io.github.tesla.filter.endpoint.plugin.request.CreateTokenRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 15:45
 * @description:
 */
@CreateTokenType(tokenType = "jwt", definitionClazz = CreateJwtTokenDefinition.class)
public class CreateJwtTokenRequestPlugin extends CreateTokenRequestPlugin {
    private final int EXPIRY_SECONDS = 7200;

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        CreateJwtTokenDefinition tokenDefinition =
            JsonUtils.json2Definition(filterParam, CreateJwtTokenDefinition.class);
        return hs256TokenGenerator(servletRequest, realHttpObject, tokenDefinition);
    }

    private HttpResponse hs256TokenGenerator(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        CreateJwtTokenDefinition tokenDefinition) {
        String jwtExpiresStr = servletRequest.getHeader(tokenDefinition.getExpiresHeaderKey());
        int expirySeconds = EXPIRY_SECONDS;
        if (null != jwtExpiresStr) {
            expirySeconds = Integer.parseInt(jwtExpiresStr.trim());
        }

        // create JWT Token
        final Date current = new Date();
        JWTCreator.Builder builder = JWT.create().withIssuer(tokenDefinition.getIssuer()).withIssuedAt(current)
            .withExpiresAt(DateUtils.addSeconds(current, expirySeconds));

        // having claims to add JWT payload
        final String claims = servletRequest.getHeader(tokenDefinition.getClaimsHeaderKey());
        LOGGER.info("claims data: {}", claims);
        if (null != claims) {
            LOGGER.info("Header '" + tokenDefinition.getClaimsHeaderKey() + "' exist");
            try {
                // Parse claims values
                // Base64
                String claimsData = new String(Base64.getDecoder().decode(claims), Charset.forName("UTF-8"));
                final String[] claimArray = claimsData.split(";");
                for (String claim : claimArray) {
                    String[] keyValue = claim.trim().split(":");
                    builder.withClaim(keyValue[0].trim(), DESTools.encrypt(keyValue[1].trim()));
                }
            } catch (Throwable e) {
                PluginUtil.writeFilterLog(CreateJwtTokenRequestPlugin.class, e.getMessage(), e);
                LOGGER.info(e.getMessage());
                return PluginUtil.createResponse(HttpResponseStatus.FORBIDDEN, servletRequest.getNettyRequest(),
                    e.getMessage());
            }
        }
        // generator JWT Token
        try {
            final String token = builder.sign(Algorithm.HMAC256(tokenDefinition.getSecretKey()));
            LOGGER.info("Jwt Token: {}", token);

            // Set Header Transfer After biz
            final FullHttpRequest realRequest = (FullHttpRequest)httpObject;
            realRequest.headers().add(tokenDefinition.getTokenHeaderKey(), token);
            return null;
        } catch (Throwable e) {
            PluginUtil.writeFilterLog(CreateJwtTokenRequestPlugin.class, e.getMessage(), e);
            LOGGER.info(e.getMessage());
            return PluginUtil.createResponse(HttpResponseStatus.FORBIDDEN, servletRequest.getNettyRequest(),
                e.getMessage());
        }
    }
}
