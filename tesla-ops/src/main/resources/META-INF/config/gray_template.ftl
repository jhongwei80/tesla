import com.jayway.jsonpath.JsonPath;
def choose(request){
Map<String, Object> node = new HashMap<>();
def header = request.get("header");
def parameter = request.get("parameter");
def body = request.get("body");
<#list nodes as item >
    node.put("${item.paramKey}","${item.paramValue}");
</#list>

<#assign condition="" />
<#list conditions as item >
    <#if item.paramKind == 'HTTP_HEADER'>
        <#assign condition += 'header.get("'+item.paramKey?lower_case+'") == "'+item.paramValue+'" && ' />
    </#if>
    <#if item.paramKind == 'HTTP_PARAM'>
        <#assign condition +='parameter.get("'+item.paramKey+'") == "'+item.paramValue+'" && ' />
    </#if>
    <#if item.paramKind == 'HTTP_BODY'>
        <#assign condition +='JsonPath.parse(body).read("\\$.'+item.paramKey+'") == "'+item.paramValue+'" && ' />
    </#if>
</#list>
    def match = "false";
    try{
    if(${condition[0..condition?length-5]}){
match = "true";
}
}catch(Exception e){
}
node.put("match",match);
return node;
}