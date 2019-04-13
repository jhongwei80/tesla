/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.filter.service.plugin.request.token;

import static io.github.tesla.auth.sdk.jwt.impl.PublicClaims.*;

import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;

import io.github.tesla.auth.sdk.jwt.JWT;
import io.github.tesla.auth.sdk.jwt.algorithms.Algorithm;
import io.github.tesla.auth.sdk.jwt.exceptions.TokenExpiredException;
import io.github.tesla.auth.sdk.jwt.interfaces.Claim;
import io.github.tesla.auth.sdk.oauth2.OAuthJwtRazor;
import io.github.tesla.auth.sdk.signer.Constants;
import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.JWTTokenDefinition;
import io.github.tesla.filter.service.plugin.request.AuthRequestPlugin;
import io.github.tesla.filter.support.ResponseMessage;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 16:59
 * @description:
 */
@AuthType(authType = "jwt", definitionClazz = JWTTokenDefinition.class)
public class JwtTokenRequestPlugin extends AuthRequestPlugin {
    public static final String AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    private static final Set<String> JWT_IGNORE_CLAIMS_KEY =
        ImmutableSet.of(SUBJECT, NOT_BEFORE, AUDIENCE, ISSUER, EXPIRES_AT, ISSUED_AT, JWT_ID);

    private void checkTokenAndSetRequest(HttpObject httpObject, String token, JWTTokenDefinition jwtTokenDefinition)
        throws Exception {
        // verify JWT token
        JWT.require(Algorithm.HMAC256(jwtTokenDefinition.getSecretKey())).withIssuer(jwtTokenDefinition.getIssuer())
            .acceptIssuedAt(60).acceptNotBefore(60).build().verify(token);
        Map<String, Claim> claimMap = JWT.decode(token).getClaims();
        String claims = OAuthJwtRazor.claimRebuild(claimMap, JWT_IGNORE_CLAIMS_KEY);
        if (YesOrNoEnum.YES.getCode().equals(jwtTokenDefinition.getParseClaims()) && !StringUtils.isEmpty(claims)) {
            claims2TransferHeader(claims, httpObject, jwtTokenDefinition.getClaimsHeaderKey());
        }
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        JWTTokenDefinition jwtTokenDefinition = JsonUtils.fromJson((String)filterParam, JWTTokenDefinition.class);
        LOGGER.info("tokenHeaderKey" + ": {}", servletRequest.getHeader(AUTHORIZATION));
        // 1: find Authorization: 默认 Bearer
        String authorizationHeaderValue = servletRequest.getHeader(AUTHORIZATION);

        ResponseMessage message = null;
        if (!StringUtils.isEmpty(authorizationHeaderValue)) {
            LOGGER.info("Authorization: {}", authorizationHeaderValue);
            if (authorizationHeaderValue.startsWith(AUTHORIZATION_HEADER_VALUE_PREFIX)) {
                final String token = //
                    authorizationHeaderValue.substring(AUTHORIZATION_HEADER_VALUE_PREFIX.length()).trim();
                try {
                    checkTokenAndSetRequest(realHttpObject, token, jwtTokenDefinition);
                    return null;
                } catch (TokenExpiredException e) {
                    PluginUtil.writeFilterLog(JwtTokenRequestPlugin.class, e.getMessage(), e);
                    LOGGER.info(e.getMessage());
                    message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG001, e.getMessage());
                    return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
                        JSON.toJSONString(message));
                } catch (Throwable e) {
                    PluginUtil.writeFilterLog(JwtTokenRequestPlugin.class, e.getMessage(), e);
                    message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG002, e.getMessage());
                }
            }

        } else {
            message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG003,
                "There are at least one header '" + Constants.AUTHORIZATION + "' expected");
        }
        return PluginUtil.createResponse(HttpResponseStatus.FORBIDDEN, servletRequest.getNettyRequest(),
            JSON.toJSONString(message));

    }

}
