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

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.waf.definition.BlackURLDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Black url filter
 */
@WafRequestPlugin(filterType = "BlackURLRequestPlugin", filterOrder = 4, definitionClazz = BlackURLDefinition.class,
    filterName = "URL黑名单过滤插件")
public class BlackURLRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BlackURLDefinition configBean = JsonUtils.json2Definition(filterParam, BlackURLDefinition.class);
        if (configBean == null) {
            return null;
        }
        final HttpRequest nettyRequst = servletRequest.getNettyRequest();
        String uri = servletRequest.getRequestURI();
        int index = uri.indexOf("?");
        if (index > -1) {
            uri = uri.substring(0, index);
        }
        List<String> patterns = configBean.getBlackURLs();
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                super.writeFilterLog(BlackURLRequestPlugin.class, uri + " match " + pattern.pattern());
                return super.createResponse(HttpResponseStatus.FORBIDDEN, nettyRequst);
            }
        }
        return null;
    }

}
