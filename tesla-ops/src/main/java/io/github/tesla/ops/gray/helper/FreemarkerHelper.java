package io.github.tesla.ops.gray.helper;

import java.io.StringWriter;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import io.github.tesla.ops.common.TeslaException;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/11/1 18:36
 * @version: V1.0.0
 * @since JDK 11
 */
public class FreemarkerHelper {

    private static final Logger log = LoggerFactory.getLogger(FreemarkerHelper.class);

    private final static StringTemplateLoader templateHolder = new StringTemplateLoader();

    private final static Configuration configuration;

    static {
        Configuration configuration_ = new Configuration(Configuration.VERSION_2_3_28);
        configuration_.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
        // configuration_.setOutputFormat(JSONOutputFormat.INSTANCE);
        configuration_.setTemplateLoader(templateHolder);
        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
        owb.setIterableSupport(true);
        configuration_.setObjectWrapper(owb.build());
        configuration = configuration_;
    }

    /**
     * 执行Groovy脚本
     * 
     * @param groovy
     *            脚本
     * @param funcName
     *            函数名
     * @param args
     *            参数
     * @return
     */
    public static Object executeGroovy(String groovy, String funcName, Object... args) {
        try {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            engine.eval(groovy, engine.createBindings());
            Invocable invocable = (Invocable)engine;
            return invocable.invokeFunction(funcName, args);
        } catch (Exception e) {
            log.error("execute groovy script error.", e);
            throw new TeslaException(e.getMessage());
        }
    }

    /**
     * 生成freemarker脚本
     * 
     * @param templateId
     * @param tempalteContent
     * @param templateParam
     * @return
     */
    public static String generate(String templateId, String tempalteContent, Map<String, Object> templateParam) {
        log.debug("execute freemarker template:{}", templateId);
        try {
            templateHolder.putTemplate(templateId, tempalteContent);
            Template template = configuration.getTemplate(templateId);
            StringWriter transformedWriter = new StringWriter();
            template.process(templateParam, transformedWriter);
            return transformedWriter.toString();
        } catch (Exception e) {
            log.error("execute freemarker template error.", e);
            throw new TeslaException(e.getMessage());
        }
    }

    /*
    public static void main(String[] args) throws Exception {
        //freemarker
        String ftlFilePath = "D:/bkjk-workspace/tesla/tesla-ops/src/main/resources/META-INF/config/rules/gray_rule_groovy.ftl";
        Map<String, Object> param = Maps.newHashMap();
        GrayPolicyConditionDO condition1 = new GrayPolicyConditionDO();
        condition1.setParamKind(GrayParamKind.HTTP_HEADER.getCode());
        condition1.setParamKey("X-BKJK-UseGray");
        condition1.setParamValue("yes");
    
        GrayPolicyConditionDO condition2 = new GrayPolicyConditionDO();
        condition2.setParamKind(GrayParamKind.HTTP_PARAM.getCode());
        condition2.setParamKey("X-BKJK-Token");
        condition2.setParamValue("abcdefg");
    
        GrayPolicyConditionDO condition3 = new GrayPolicyConditionDO();
        condition3.setParamKind(GrayParamKind.HTTP_BODY.getCode());
        condition3.setParamKey("memberId");
        condition3.setParamValue("123456789");
        param.put("conditions", Arrays.asList(condition1,condition2,condition3));
    
        GrayPolicyConditionDO node1 = new GrayPolicyConditionDO();
        node1.setParamKind(GrayParamKind.NODE.getCode());
        node1.setParamKey("version");
        node1.setParamValue("1.0.0");
    
        GrayPolicyConditionDO node2 = new GrayPolicyConditionDO();
        node2.setParamKind(GrayParamKind.NODE.getCode());
        node2.setParamKey("group");
        node2.setParamValue("com.bkjk.platform");
        param.put("nodes",Arrays.asList(node1,node2));
    
        String freemarker = new String(Files.readAllBytes(Paths.get(ftlFilePath)));
        String groovy = FreemarkerHelper.generate("test",freemarker,param);
        System.out.println(groovy);
        System.out.println("**********************我是分割线**********************");
    
        JSONObject request = new JSONObject();
    
        JSONObject header = new JSONObject();
        header.put("x-bkjk-usegray","yes");
        request.put("header",header);
    
        JSONObject parameter = new JSONObject();
        parameter.put("X-BKJK-Token","abcdefg");
        request.put("parameter",parameter);
    
        JSONObject body = new JSONObject();
        body.put("memberId","123456789");
        request.put("body",body);
    
        request.put("contentType","application/json");
    
        Object result = FreemarkerHelper.executeGroovy(groovy,"choose",request);
        System.out.println(result);
    }
    */

}
