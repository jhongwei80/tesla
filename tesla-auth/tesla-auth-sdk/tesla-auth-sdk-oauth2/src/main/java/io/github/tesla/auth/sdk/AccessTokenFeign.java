package io.github.tesla.auth.sdk;

import java.util.Map;

import feign.Body;
import feign.HeaderMap;
import feign.Param;
import feign.RequestLine;
import io.github.tesla.auth.common.support.APIResult;

interface AccessTokenFeign {
    @RequestLine("POST /v1/oauth2/token")
    @Body(value = "%7B\"data\":\"{code}\"%7D")
    APIResult apply( //
        @HeaderMap Map<String, Object> headers, @Param("code") String code);

    @RequestLine("POST /v1/oauth2/verifyToken")
    @Body(value = "%7B\"data\":\"{code}\"%7D")
    APIResult checked( //
        @HeaderMap Map<String, Object> headers, @Param("code") String code);
}