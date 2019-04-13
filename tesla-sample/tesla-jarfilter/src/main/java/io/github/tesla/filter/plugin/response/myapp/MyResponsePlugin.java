package io.github.tesla.filter.plugin.response.myapp;

import io.github.tesla.filter.endpoint.plugin.response.JarExecuteResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @Auther: zhipingzhang
 * @Date: 2018/10/29 11:21:
 * @Description: 需要自己编写的入请求过滤器代码，当前demo的类路径为 io.github.tesla.filter.plugin.response.myapp
 * 自己编写时，包路径中的myapp换成自己的项目名称, 类名 MyResponsePlugin 也尽量命令为和过滤器规则相符合的名称
 * <p>
 * 请注意，myapp下面的plugin将不会被扫描到，请不要在该demo目录下编写自己的规则
 */
public class MyResponsePlugin extends JarExecuteResponsePlugin {
    /**
     * 功能描述: 具体的过滤规则代码
     *
     * @parmname: doFilter
     * @param: [servletRequest, httpResponse]
     * @return: io.netty.handler.codec.http.HttpResponse
     * 实现具体filter规则，如果返回null，则会继续走之后的filter,如果返回了response，则会中断后续filter直接返回给调用方
     * @auther: zhipingzhang
     * @date: 2018/10/29 11:27
     */
    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {
        // load jar and exec
        System.out.println("i'm jar requestFilter");
        return httpResponse;

    }

}
