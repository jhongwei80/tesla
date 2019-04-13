package io.github.tesla.filter.waf.plugin.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.waf.definition.BlackURLParamDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Black url param filter
 */
@WafRequestPlugin(filterType = "BlackURLParamRequestPlugin", definitionClazz = BlackURLParamDefinition.class,
    filterOrder = 5, filterName = "URL参数黑名单过滤插件")
public class BlackURLParamRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BlackURLParamDefinition configBean = JsonUtils.json2Definition(filterParam, BlackURLParamDefinition.class);
        if (configBean == null) {
            return null;
        }
        String url = null;
        try {
            String uri = servletRequest.getRequestURL().toString();
            url = URLDecoder.decode(uri, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> patterns = configBean.getBlackURLParams();
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                super.writeFilterLog(BlackURLParamRequestPlugin.class, url + " match " + pattern.pattern());
                return super.createResponse(HttpResponseStatus.FORBIDDEN, servletRequest.getNettyRequest());
            }
        }
        return null;
    }
}
