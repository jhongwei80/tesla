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
package io.github.tesla.filter.waf.plugin.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.waf.definition.BlackCookieDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * cooki黑名单过滤
 */
@WafRequestPlugin(filterType = "BlackCookieRequestPlugin", definitionClazz = BlackCookieDefinition.class,
    filterOrder = 1, filterName = "cookie值黑名单过滤插件")
public class BlackCookieRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        BlackCookieDefinition configBean = JsonUtils.json2Definition(filterParam, BlackCookieDefinition.class);
        if (configBean == null) {
            return null;
        }
        final HttpRequest nettyRequst = servletRequest.getNettyRequest();
        Cookie[] cookies = servletRequest.getCookies();
        List<String> patterns = configBean.getBlackCookies();
        if (cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String cookieValue = cookie.getValue();
                for (String patternString : patterns) {
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(cookieValue);
                    if (matcher.find()) {
                        super.writeFilterLog(BlackCookieRequestPlugin.class,
                            cookieValue + " match " + pattern.pattern());
                        return super.createResponse(HttpResponseStatus.FORBIDDEN, nettyRequst);
                    }
                }
            }
        }
        return null;
    }
}
