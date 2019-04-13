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

import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.waf.definition.BlackUaDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Black user-agent filter
 */
@WafRequestPlugin(filterType = "BlackUaRequestPlugin", definitionClazz = BlackUaDefinition.class, filterOrder = 3,
    filterName = "UA黑名单过滤插件")
public class BlackUaRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BlackUaDefinition configBean = JsonUtils.json2Definition(filterParam, BlackUaDefinition.class);
        if (configBean == null) {
            return null;
        }
        final HttpRequest nettyRequst = servletRequest.getNettyRequest();
        Enumeration<String> userAgents = servletRequest.getHeaders("User-Agent");
        List<String> patterns = configBean.getBlackUas();
        while (userAgents.hasMoreElements()) {
            String userAgent = userAgents.nextElement();
            for (String patternStr : patterns) {
                if (StringUtils.isBlank(patternStr)) {
                    continue;
                }
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(userAgent);
                if (matcher.find()) {
                    super.writeFilterLog(BlackUaRequestPlugin.class, userAgent + " match " + pattern.pattern());
                    return super.createResponse(HttpResponseStatus.FORBIDDEN, nettyRequst);
                }
            }
        }
        return null;
    }

}
