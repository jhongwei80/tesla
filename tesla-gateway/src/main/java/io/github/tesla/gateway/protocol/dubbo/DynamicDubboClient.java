package io.github.tesla.gateway.protocol.dubbo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.JSONOutputFormat;
import freemarker.core.ParseException;
import freemarker.template.*;
import io.github.tesla.filter.endpoint.definition.DubboRpcRoutingDefinition;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;

/**
 * @author liushiming
 * @version DynamicDubboClient.java, v 0.0.1 2018年1月29日 下午2:38:28 liushiming
 */
public class DynamicDubboClient {

    private final ApplicationConfig applicationConfig;

    private final RegistryConfig registryConfig;

    private final StringTemplateLoader templateHolder = new StringTemplateLoader();

    private final Configuration configuration;

    public DynamicDubboClient(final ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
        super();
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
        Configuration configuration_ = new Configuration(Configuration.VERSION_2_3_26);
        configuration_.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_26));
        configuration_.setOutputFormat(JSONOutputFormat.INSTANCE);
        configuration_.setTemplateLoader(templateHolder);
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_26);
        owb.setIterableSupport(true);
        configuration_.setObjectWrapper(owb.build());
        this.configuration = configuration_;
    }

    private String buildFreemarkerTemplate(final String templateContent) {
        Object templateJson = JSON.parse(templateContent);
        StringBuilder sb = new StringBuilder();
        sb.append("<#assign jsonObj = input.path(\"$\")>");
        sb.append("<#assign jsonStr = input.json(\"$\")>");
        sb.append("{");
        if (templateJson instanceof JSONArray) {
            for (Iterator<Object> it = ((JSONArray)templateJson).iterator(); it.hasNext();) {
                JSONObject jsonObj = (JSONObject)it.next();
                sb.append("\"" + jsonObj.getString("type") + "\"");
                sb.append(":");
                sb.append("\"" + jsonObj.getString("expression") + "\"");
                if (it.hasNext()) {
                    sb.append(",");;
                }
            }
        } else if (templateJson instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject)templateJson;
            sb.append(jsonObj.getString("type"));
            sb.append(":");
            sb.append("\"" + jsonObj.getString("expression") + "\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private String cacheTemplate(final DubboRpcRoutingDefinition definition) {
        final String templateContent = this.buildFreemarkerTemplate(definition.getDubboParamTemplate());
        final String templateKey = definition.getServiceName() + "_" + definition.getMethodName();
        templateHolder.putTemplate(templateKey, templateContent);
        return templateKey;
    }

    private String doDataMapping(final String templateKey, final NettyHttpServletRequest servletRequest)
        throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
        TemplateException {
        Map<String, Object> templateContext = new HashMap<String, Object>();
        BodyMapping body = new BodyMapping(servletRequest);
        body.shouldReplace();
        templateContext.put("header", new HeaderMapping(servletRequest));
        templateContext.put("input", body);
        Template template = configuration.getTemplate(templateKey);
        StringWriter outPutWrite = new StringWriter();
        template.process(templateContext, outPutWrite);
        String outPutJson = outPutWrite.toString();
        return outPutJson;
    }

    public String doRpcRemoteCall(final DubboRpcRoutingDefinition definition,
        final NettyHttpServletRequest servletRequest) {
        try {
            final String serviceName = definition.getServiceName();
            final String methodName = definition.getMethodName();
            final String group = definition.getGroup();
            final String version = definition.getVersion();
            ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(serviceName);
            reference.setGroup(group);
            reference.setGeneric(true);
            reference.setCheck(false);
            reference.setVersion(version);
            ReferenceConfigCache cache = ReferenceConfigCache.getCache();
            GenericService genericService = cache.get(reference);
            String templateKey = this.cacheTemplate(definition);
            Pair<String[], Object[]> typeAndValue = this.transformerData(templateKey, servletRequest);
            Object response = genericService.$invoke(methodName, typeAndValue.getLeft(), typeAndValue.getRight());
            return JSON.toJSONString(response);
        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format(
                "service definition is wrong,please check the proto file you update,service is %s, method is %s",
                definition.getServiceName(), definition.getMethodName()), e);
        }

    }

    private Pair<String[], Object[]> transformerData(String templateKey, final NettyHttpServletRequest servletRequest)
        throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
        TemplateException {
        String outPutJson = this.doDataMapping(templateKey, servletRequest);
        Map<String, String> dubboParamters =
            JSON.parseObject(outPutJson, new TypeReference<HashMap<String, String>>() {});
        List<String> type = Lists.newArrayList();
        List<Object> value = Lists.newArrayList();
        for (Map.Entry<String, String> entry : dubboParamters.entrySet()) {
            String type_ = entry.getKey();
            String value_ = entry.getValue();
            type.add(type_);
            if (type_.startsWith("java")) {
                value.add(value_);
            } else {
                Map<String, String> value_map =
                    JSON.parseObject(value_, new TypeReference<HashMap<String, String>>() {});
                value_map.put("class", type_);
                value.add(value_map);
            }
        }
        String[] typeArray = new String[type.size()];
        type.toArray(typeArray);
        return new ImmutablePair<String[], Object[]>(typeArray, value.toArray());
    }

}
