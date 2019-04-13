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
import io.github.tesla.filter.waf.definition.BlackIpDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * ip黑名单过滤
 */
@WafRequestPlugin(filterType = "BlackIpRequestPlugin", definitionClazz = BlackIpDefinition.class, filterOrder = 2,
    filterName = "IP黑名单过滤插件")
public class BlackIpRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BlackIpDefinition configBean = JsonUtils.json2Definition(filterParam, BlackIpDefinition.class);
        if (configBean == null) {
            return null;
        }
        final HttpRequest nettyRequst = servletRequest.getNettyRequest();
        String realIp = servletRequest.getHeader("X-Real-IP");
        if (realIp != null) {
            List<String> patterns = configBean.getBlackIps();
            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(realIp.toLowerCase());
                if (matcher.find()) {
                    super.writeFilterLog(BlackIpRequestPlugin.class, realIp + " match " + pattern.pattern());
                    return super.createResponse(HttpResponseStatus.FORBIDDEN, nettyRequst);
                }
            }
        }
        return null;
    }

}
