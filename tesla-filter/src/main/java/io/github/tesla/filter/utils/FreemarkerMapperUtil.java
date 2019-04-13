package io.github.tesla.filter.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.JSONOutputFormat;
import freemarker.template.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class FreemarkerMapperUtil {

    protected static class BodyMapping {
        private final String body;

        private final Object document;

        BodyMapping(Map<String, Object> request) throws IOException {
            this.body = JSON.toJSONString(request);
            this.document = com.jayway.jsonpath.Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .build().jsonProvider().parse(body);

        }

        BodyMapping(String request) throws IOException {
            this.body = request;
            this.document = com.jayway.jsonpath.Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .build().jsonProvider().parse(body);

        }

        /**
         * <pre>
         * 此函数计算 JSONPath 表达式并以 JSON 字符串形式返回结果。
         * 例如，$input.json('$.pets') 将返回一个表示宠物结构的 JSON 字符串。
         * </pre>
         */
        public String json(String expression) {
            Object json = path(expression);
            if (json instanceof String) {
                return (String)json;
            } else {
                return JSON.toJSONString(json);
            }
        }

        public String params() {
            return this.body;
        }

        /**
         * <pre>
         * 获取一个 JSONPath 表达式字符串 (x) 并返回结果的对象表示形式。
         * 这样，您便可通过 FreeMarker 模板语言 (VTL) 在本机访问和操作负载的元素。
         * </pre>
         */
        public Object path(String expression) {
            Object obj = JsonPath.parse(document).read(expression);
            return obj;
        }

        @Override
        public String toString() {
            return this.body;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerMapperUtil.class);

    private static final StringTemplateLoader templateHolder = new StringTemplateLoader();

    private static final Configuration configuration;

    static {
        Configuration config = new Configuration(Configuration.VERSION_2_3_28);
        config.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
        config.setOutputFormat(JSONOutputFormat.INSTANCE);
        config.setTemplateLoader(templateHolder);
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
        owb.setIterableSupport(true);
        config.setObjectWrapper(owb.build());
        configuration = config;
    }

    /**
     * 格式化HTTP请求头、请求参数 如报文头为{"name":"bkjk","token":"123456"},则在模板中使用${header.name}即可获得
     * 如报文参数为{"name":"bkjk","token":"123456"},则在模板中使用${parameter.name}即可获得
     * 如报文体为User{"name":"bkjk","token":"123456"},则在模板中使用${body.name}即可获得
     *
     * @param request
     * @param ftlTemplate
     *            Freemarker模板
     * @return
     */
    public static String format(HttpServletRequest request, Object requestBody, String ftlTemplate) {
        return FreemarkerMapperUtil.format(request, JSON.toJSONString(requestBody), ftlTemplate);
    }

    /**
     * 格式化HTTP请求头、请求参数 如报文头为{"name":"bkjk","token":"123456"},则在模板中使用${header.name}即可获得
     * 如报文参数为{"name":"bkjk","token":"123456"},则在模板中使用${parameter.name}即可获得
     * 如报文体为{"name":"bkjk","token":"123456"},则在模板中使用${body.name}即可获得
     *
     * @param request
     * @param ftlTemplate
     *            Freemarker模板
     * @return
     */
    public static String format(HttpServletRequest request, String requestBody, String ftlTemplate) {
        if (Objects.isNull(request) || StringUtils.isEmpty(requestBody) || StringUtils.isEmpty(ftlTemplate)) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> headers = Maps.newHashMap();
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()) {
            String key = headerEnumeration.nextElement();
            headers.put(key, request.getHeader(key));
        }

        Map<String, Object> parameters = Maps.newHashMap();
        Enumeration<String> paramEnumeration = request.getParameterNames();
        while (paramEnumeration.hasMoreElements()) {
            String key = paramEnumeration.nextElement();
            parameters.put(key, request.getParameter(key));
        }
        if (MapUtils.isEmpty(headers) || MapUtils.isEmpty(parameters)) {
            return StringUtils.EMPTY;
        }
        String templateNo = "templateHPB" + System.nanoTime();
        try {
            templateHolder.putTemplate(templateNo, ftlTemplate);
            Map<String, Object> templateContext = new HashMap<String, Object>();
            BodyMapping headerMapping = new BodyMapping(headers);
            BodyMapping paramMapping = new BodyMapping(parameters);
            BodyMapping bodyMapping = new BodyMapping(requestBody);
            templateContext.put("header", headerMapping.path("$"));
            templateContext.put("parameter", paramMapping.path("$"));
            templateContext.put("body", bodyMapping.path("$"));
            Template template = configuration.getTemplate(templateNo);
            StringWriter transformedWriter = new StringWriter();
            template.process(templateContext, transformedWriter);
            return transformedWriter.toString();
        } catch (TemplateException | IOException e) {
            logger.error("The reuqest format error, template:{}", templateNo, e);
            throw new RuntimeException("The request format error", e);
        }
    }

    /**
     * 格式化json数据 如原数据为{"name":"bkjk"},则在模板中使用${body.name}即可获得
     *
     * @param request
     *            原数据
     * @param ftlTemplate
     *            格式化模板(Freemarker)
     * @return
     */
    public static String formatForJsonPath(String request, String ftlTemplate) {
        if (StringUtils.isEmpty(request)) {
            return request;
        }
        String templateNo = MD5Util.md5(request);
        try {
            templateHolder.putTemplate(templateNo, ftlTemplate);
            Map<String, Object> templateContext = new HashMap<String, Object>();
            BodyMapping bodyMapping = new BodyMapping(request);
            templateContext.put("body", bodyMapping.path("$"));
            Template template = configuration.getTemplate(templateNo);
            StringWriter transformedWriter = new StringWriter();
            template.process(templateContext, transformedWriter);
            return transformedWriter.toString();
        } catch (TemplateException | IOException e) {
            logger.error("The reuqest format error, template:{}", templateNo, e);
            throw new RuntimeException("The request format error", e);
        }
    }

    /**
     * 格式化HTTP请求头、请求参数 如报文头为{"name":"bkjk","token":"123456"},则在模板中使用${header.name}即可获得
     * 如报文参数为{"name":"bkjk","token":"123456"},则在模板中使用${parameter.name}即可获得
     *
     * @param request
     * @param ftlTemplate
     *            Freemarker模板
     * @return
     */
    public static String formatForRequestWithoutBody(HttpServletRequest request, String ftlTemplate) {
        if (Objects.isNull(request) || StringUtils.isEmpty(ftlTemplate)) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> headers = Maps.newHashMap();
        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()) {
            String key = headerEnumeration.nextElement();
            headers.put(key, request.getHeader(key));
        }

        Map<String, Object> parameters = Maps.newHashMap();
        Enumeration<String> paramEnumeration = request.getParameterNames();
        while (paramEnumeration.hasMoreElements()) {
            String key = paramEnumeration.nextElement();
            parameters.put(key, request.getParameter(key));
        }
        if (MapUtils.isEmpty(headers) || MapUtils.isEmpty(parameters)) {
            return StringUtils.EMPTY;
        }
        String templateNo = "templateHP" + System.nanoTime();
        try {
            templateHolder.putTemplate(templateNo, ftlTemplate);
            Map<String, Object> templateContext = new HashMap<String, Object>();
            BodyMapping headerMapping = new BodyMapping(headers);
            BodyMapping paramMapping = new BodyMapping(parameters);
            templateContext.put("header", headerMapping.path("$"));
            templateContext.put("parameter", paramMapping.path("$"));
            Template template = configuration.getTemplate(templateNo);
            StringWriter transformedWriter = new StringWriter();
            template.process(templateContext, transformedWriter);
            return transformedWriter.toString();
        } catch (TemplateException | IOException e) {
            logger.error("The reuqest format error, template:{}", templateNo, e);
            throw new RuntimeException("The request format error", e);
        }
    }

    public static Boolean isCanDataMapping(ByteBuf contentBuf) {
        String contentStr = null;
        try {
            contentStr = contentBuf.toString(CharsetUtil.UTF_8);
            return JsonUtils.isJson(contentStr);
        } catch (Throwable e) {
            logger.error("body transform error , body not json ,body content：" + contentStr, e);
            return false;
        }
    }

}
