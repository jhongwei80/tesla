package io.github.tesla.gateway.cache;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import groovy.lang.GroovyObject;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.GroovyCompiler;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.util.CharsetUtil;

@Component
public class GrayRuesCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrayRuesCache.class);

    private GrayRulesBo grayRuleBo = new GrayRulesBo();
    @Value("${spring.application.name}")
    private String applicationName;

    private static GrayRuesCache grayRuesCache;

    @PostConstruct
    public void init() {
        grayRuesCache = this;
        applicationName = applicationName.toUpperCase();

    }

    public synchronized static void initCache(String grayRulesString) {
        if (StringUtils.isBlank(grayRulesString)) {
            throw new RuntimeException("grayRulesString is empty");
        }
        grayRuesCache.grayRuleBo = JsonUtils.fromJson(grayRulesString, GrayRulesBo.class);
    }

    public static String getHash() {
        Map<String, String> hashMasp = Maps.newHashMap();
        hashMasp.put("hash", grayRuesCache.grayRuleBo.getHash());
        return JsonUtils.serializeToJson(hashMasp);
    }

    public static String getAllRules() {
        return JsonUtils.serializeToJson(grayRuesCache.grayRuleBo);
    }

    public static List<String> getGrayRules(String targetAppName) {
        List<String> groovyList = Lists.newArrayList();
        List<GrayRule> grayRuleList = grayRuesCache.grayRuleBo.getGrayRules()
            .get(grayRuesCache.applicationName + "," + targetAppName.toUpperCase());
        if (grayRuleList == null || grayRuleList.isEmpty()) {
            return groovyList;
        } else {
            grayRuleList.forEach(grayRule -> {
                groovyList.add(grayRule.getGroovyScript());
            });
        }
        return groovyList;
    }

    public static List<Map<String, String>> getTargetApp(String targetAppName, HttpServletRequest httpRequest) {
        List<GrayRule> grayRuleList = grayRuesCache.grayRuleBo.getGrayRules()
            .get(grayRuesCache.applicationName + "," + targetAppName.toUpperCase());
        List<Map<String, String>> groupVersionMapList = Lists.newArrayList();
        if (grayRuleList == null || grayRuleList.isEmpty()) {
            return groupVersionMapList;
        } else {
            for (GrayRule grayRule : grayRuleList) {
                Class<?> clazz = GroovyCompiler.compile(grayRule.getGroovyScript());
                GroovyObject groovyObject = null;
                try {
                    groovyObject = (GroovyObject)clazz.getDeclaredConstructor().newInstance();
                    Object[] objects = new Object[] {convertRequest(httpRequest)};
                    Map<String, String> ruleMap = (Map<String, String>)groovyObject.invokeMethod("choose", objects);
                    if (ruleMap != null && ruleMap.size() == 1 && ruleMap.containsKey("match")) {
                        // continue;
                    } else {
                        groupVersionMapList.add(ruleMap);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return groupVersionMapList;
    }

    private static Map convertRequest(HttpServletRequest request) {
        Map<String, Object> context = new HashMap<>();
        Enumeration<String> hs = request.getHeaderNames();
        Map<String, Object> header = new HashMap<>();
        while (hs.hasMoreElements()) {
            String key = hs.nextElement();
            header.put(key.toLowerCase(), request.getHeader(key));
        }
        context.put("header", header);
        Enumeration<String> pns = request.getParameterNames();
        Map<String, Object> parameter = new HashMap<>();
        while (pns.hasMoreElements()) {
            String key = pns.nextElement();
            parameter.put(key.toLowerCase(), request.getParameter(key));
        }
        context.put("parameter", parameter);
        try {
            NettyHttpServletRequest nettyRequest = (NettyHttpServletRequest)request;
            if (StringUtils.isNotBlank(nettyRequest.getContentType())
                && nettyRequest.getContentType().toLowerCase().contains("application/json")
                && nettyRequest.getRequestBody() != null) {
                String body = new String(nettyRequest.getRequestBody(), CharsetUtil.UTF_8);
                if (StringUtils.isNotBlank(body) && JsonUtils.isJson(body)) {
                    context.put("body", body);
                }
            }
        } catch (Exception e) {
        }
        context.putIfAbsent("body", JsonUtils.serializeToJson(Maps.newHashMap()));
        return context;
    }
}

class GrayRulesBo {
    private String hash;
    private Map<String, List<GrayRule>> grayRules;

    public GrayRulesBo() {
        this.hash = "";
        this.grayRules = Maps.newConcurrentMap();
    }

    public String getHash() {
        return hash;
    }

    public Map<String, List<GrayRule>> getGrayRules() {
        return grayRules;
    }

    public void setGrayRules(Map<String, List<GrayRule>> grayRules) {
        this.grayRules = grayRules;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}

class GrayRule {

    private List<String> headers;

    public List<String> getHeader() {
        return headers;
    }

    public void setHeader(List<String> headers) {
        this.headers = headers;
    }

    public String getGroovyScript() {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript) {
        this.groovyScript = groovyScript;
    }

    private String groovyScript;

}
