package io.github.tesla.ops.common;

import java.io.IOException;

import javax.servlet.*;

import org.apache.shiro.SecurityUtils;

public class DataSourceFilter implements Filter {
    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        if (SecurityUtils.getSubject() != null
            && SecurityUtils.getSubject().getSession().getAttribute("datasource") != null) {
            MultiDataSourceSwitcher
                .setDataSourceKey(String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("datasource")));
        }
        filterChain.doFilter(servletRequest, servletResponse);
        MultiDataSourceSwitcher.clearDataSourceType();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
