package io.github.tesla.filter.endpoint.plugin.request.user;

import io.github.tesla.filter.endpoint.plugin.request.GroovyExecuteRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @Auther: zhipingzhang
 * @Date: 2018/10/29 11:21:
 * @Description: 需要自己编写的入请求过滤器代码，
 * 当前demo的类路径为 io.github.tesla.filter.plugin.request.user
 * 类名 UserGroovyRequestPlugin 尽量命令为和过滤器规则相符合的名称
 * <p>
 */
public class UserGroovyRequestPlugin extends GroovyExecuteRequestPlugin {

  /**
   * 功能描述: 具体的过滤规则代码
   *
   * @parmname: doFilter
   * @param: [servletRequest, realHttpObject]
   * @return: io.netty.handler.codec.http.HttpResponse
   * 实现具体filter规则，如果返回null，则会继续走之后的filter,
   * 如果返回了response，则会中断后续filter直接返回给调用方
   * @auther: zhipingzhang
   * @date: 2018/10/29 11:24
   */
  @Override
  public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject) {
    HttpResponse response = null;
    System.out.println("i'm jar requestFilter");
    return response;
  }
}
