package io.github.tesla.filter.endpoint.plugin.response.token;

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
import io.github.tesla.filter.endpoint.plugin.response.CreateTokenResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 15:45
 * @description:
 */
@CreateTokenType(tokenType = "jwt", definitionClazz = CreateJwtTokenDefinition.class)
public class CreateJwtTokenResponsePlugin extends CreateTokenResponsePlugin {
    private final int EXPIRY_SECONDS = 7200;

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        CreateJwtTokenDefinition tokenDefinition =
            JsonUtils.json2Definition(filterParam, CreateJwtTokenDefinition.class);

        FullHttpResponse fullHttpResponse = (FullHttpResponse)httpResponse;
        String responseMessage = "Header '" + tokenDefinition.getClaimsHeaderKey() + "' Not exist";
        final String claims = fullHttpResponse.headers().get(tokenDefinition.getClaimsHeaderKey());
        LOGGER.info("claims data: {}", claims);
        if (null != claims) {
            try {
                // Parse claims values
                // Base64
                String claimsData = new String(Base64.getDecoder().decode(claims), Charset.forName("UTF-8"));

                String jwtExpiresStr = servletRequest.getHeader(tokenDefinition.getExpiresHeaderKey());
                int expirySeconds = EXPIRY_SECONDS;
                if (null != jwtExpiresStr) {
                    expirySeconds = Integer.parseInt(jwtExpiresStr.trim());
                }
                final String[] claimArray = claimsData.split(";");
                // create JWT Token
                final Date current = new Date();
                JWTCreator.Builder builder = JWT.create().withIssuer(tokenDefinition.getIssuer()).withIssuedAt(current)
                    .withExpiresAt(DateUtils.addSeconds(current, expirySeconds));
                for (String claim : claimArray) {
                    String[] keyValue = claim.trim().split(":");
                    builder.withClaim(keyValue[0].trim(), DESTools.encrypt(keyValue[1].trim()));
                }
                final String token = builder.sign(Algorithm.HMAC256(tokenDefinition.getSecretKey()));
                LOGGER.info("Jwt Token: {}", token);

                // Set Header remove this
                fullHttpResponse.headers().remove(tokenDefinition.getClaimsHeaderKey());
                // header
                fullHttpResponse.headers().add(tokenDefinition.getTokenHeaderKey(), token);
                return httpResponse;
            } catch (Throwable e) {
                PluginUtil.writeFilterLog(CreateJwtTokenResponsePlugin.class, e.getMessage(), e);
                responseMessage = e.getMessage();
            }
        }
        LOGGER.info(responseMessage);
        ByteBuf responseBuffer = fullHttpResponse.content();
        String responseStr = responseBuffer.toString(CharsetUtil.UTF_8);
        return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
            responseStr);
    }
}
