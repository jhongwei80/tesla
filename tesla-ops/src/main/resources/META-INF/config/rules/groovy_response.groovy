package io.github.tesla.filter.endpoint.plugin.response.user;

import io.github.tesla.filter.endpoint.plugin.response.GroovyExecuteResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @Auther: zhipingzhang
 * @Date: 2018/10/29 11:21:
 * @Description: 需要自己编写的入请求过滤器代码，
 * 当前demo的类路径为 io.github.tesla.filter.plugin.request.user
 * 类名 UserGroovyResponsePlugin 尽量命令为和过滤器规则相符合的名称
 * <p>
 */
public class UserGroovyResponsePlugin extends GroovyExecuteResponsePlugin {

  /**
   * 功能描述: 具体的过滤规则代码
   *
   * @parmname: doFilter
   * @param: [servletRequest, realHttpObject]
   * @return: io.netty.handler.codec.http.HttpResponse
   * 实现具体filter规则，返回了response
   * @auther: zhipingzhang
   * @date: 2018/10/29 11:24
   */
  @Override
  public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {
    System.out.println("i'm groovy responseFilter");
    return httpResponse;
  }
}
