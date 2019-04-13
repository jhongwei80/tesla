def runFilter(io.github.tesla.gateway.filter.servlet.NettyHttpServletRequest servletRequest, io.netty.handler.codec.http.HttpObject httpObject) {
  List<String> exclude = new ArrayList<String>();
  exclude.add("/uus/user/auth");
  exclude.add("/uus/sso/authorize");
  exclude.add("/uus/sso/login");
  String actually = servletRequest.getRequestURI();
  String method = servletRequest.getMethod();
  if(exclude.contains(actually)){
    return false;
  }
  return true;
}
