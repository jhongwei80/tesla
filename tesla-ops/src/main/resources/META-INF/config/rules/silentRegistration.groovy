def runFilter(servletRequest, httpObject) {
  String stateless = servletRequest.getParameter("stateless");
  if(stateless!=null){
    return false;
  }else{
    return true;
  }
}