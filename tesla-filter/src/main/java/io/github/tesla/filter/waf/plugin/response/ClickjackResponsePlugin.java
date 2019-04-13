package io.github.tesla.filter.waf.plugin.response;

import static io.github.tesla.filter.support.CorsHandlerSupport.isPreflightRequest;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.annnotation.WafResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

@WafResponsePlugin(filterType = "ClickjackResponsePlugin", filterOrder = 2, filterName = "Clickjack插件")
public class ClickjackResponsePlugin extends AbstractResponsePlugin {
    private static final X_Frame_Options X_Frame_Option = X_Frame_Options.SAMEORIGIN;

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        HttpRequest request = servletRequest.getNettyRequest();
        if (!isPreflightRequest(request)) {
            httpResponse.headers().add("X-FRAME-OPTIONS", X_Frame_Option);
        }
        return httpResponse;
    }

    private enum X_Frame_Options {
        DENY, SAMEORIGIN
    }
}
