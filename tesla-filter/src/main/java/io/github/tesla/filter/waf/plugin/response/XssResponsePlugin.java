package io.github.tesla.filter.waf.plugin.response;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.annnotation.WafResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

/**
 * ClassName:XssHttpRequestFilter <br/>
 *
 * @author liushiming
 * @see
 * @since JDK 10
 */
@WafResponsePlugin(filterType = "XssResponsePlugin", filterOrder = 1, filterName = "xss防注入攻击插件")
public class XssResponsePlugin extends AbstractResponsePlugin {

    private static org.owasp.validator.html.Policy policy;

    static {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:antisamy.xml");
            for (Resource resource : resources) {
                policy = org.owasp.validator.html.Policy.getInstance(resource.getURL());
            }
        } catch (IOException | PolicyException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        if (policy != null) {
            FullHttpResponse fullHttpResonse = (FullHttpResponse)httpResponse;
            String contentType = fullHttpResonse.headers().get(HttpHeaderNames.CONTENT_TYPE);
            if (StringUtils.containsIgnoreCase(contentType, "text/html")) {
                ByteBuf responseBuffer = fullHttpResonse.content();
                String responseStr = responseBuffer.toString(CharsetUtil.UTF_8);
                AntiSamy antiSamy = new AntiSamy();
                try {
                    CleanResults cleanresult = antiSamy.scan(responseStr, policy);
                    ByteBuf bodyContent = Unpooled.copiedBuffer(cleanresult.getCleanHTML(), CharsetUtil.UTF_8);
                    fullHttpResonse.content().clear().writeBytes(bodyContent);
                    HttpUtil.setContentLength(fullHttpResonse, bodyContent.readerIndex());
                } catch (ScanException | PolicyException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return fullHttpResonse;
            }
        }
        return httpResponse;
    }

}
