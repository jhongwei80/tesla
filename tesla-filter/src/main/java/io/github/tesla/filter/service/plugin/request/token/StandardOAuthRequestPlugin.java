package io.github.tesla.filter.service.plugin.request.token;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.StandardOauthDefinition;
import io.github.tesla.filter.service.plugin.request.AuthRequestPlugin;
import io.github.tesla.filter.support.ResponseMessage;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 14:32
 * @description: auth filter之Oauth的实现
 */
@AuthType(authType = "standardOauth", definitionClazz = StandardOauthDefinition.class)
public class StandardOAuthRequestPlugin extends AuthRequestPlugin {
    private static final String OAUTH_ACCESS_TOKEN = "access_token";

    private static final String EXPIRED_TOKEN = "expired_token";

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        // 从JSON中转出自己需要的类型
        StandardOauthDefinition definition = JsonUtils.fromJson((String)filterParam, StandardOauthDefinition.class);
        if (definition == null) {
            return null;
        }
        ResponseMessage message;
        if (StringUtils.isBlank(servletRequest.getHeader(definition.getTokenHeader()))) {
            message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG003,
                definition.getTokenHeader() + " has not exist ");
            return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
                JSON.toJSONString(message));
        }
        String token = servletRequest.getHeader(definition.getTokenHeader());
        Map<String, String> headerMap = Maps.newHashMap();

        if (token.startsWith("bearer ")) {
            headerMap.put("Authorization", token);
        } else {
            headerMap.put(OAUTH_ACCESS_TOKEN, token);
        }
        Headers headers = Headers.of(headerMap);

        OkHttpClient okHttpClient = new OkHttpClient();
        Response response;

        String httpUrl = AntMatchUtil.concatPath(definition.getOauthServerUrl(), "/oauth/check_token");
        Request.Builder builder = new Request.Builder().url(httpUrl);
        if (headers != null) {
            builder.headers(headers);
        }
        Request request = builder.get().build();
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return null;
            } else {
                Map<String, String> errorMap = JsonUtils.fromJson(response.body().string(), Map.class);

                String errorCode = errorMap.get("error");
                String errorDesc = errorMap.get("error_description");
                if (errorCode.equals(EXPIRED_TOKEN)) {
                    message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG001, errorDesc);
                } else {
                    message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG002, errorDesc);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            PluginUtil.writeFilterLog(StandardOAuthRequestPlugin.class, e.getMessage());
            message = new ResponseMessage(ResponseMessage.MesageCodeType.OSG002, e.getMessage());
        }
        return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
            JSON.toJSONString(message));

    }

}
