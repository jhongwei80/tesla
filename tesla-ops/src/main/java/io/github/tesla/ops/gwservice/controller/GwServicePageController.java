/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.ops.gwservice.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.filter.utils.FreemarkerMapperUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Constant;
import io.github.tesla.ops.common.MultiGatewayUrlSwitcher;
import io.github.tesla.ops.gwservice.service.GatewayService;
import io.github.tesla.ops.gwservice.vo.ServiceVO;
import io.github.tesla.ops.utils.EurekaClientUtil;
import io.github.tesla.ops.utils.FileUtil;

/**
 * @author liushiming
 * @version Api.java, v 0.0.1 2018年1月9日 上午11:19:14 liushiming
 */
@Controller
@RequestMapping("gateway/service")
public class GwServicePageController extends BaseController {

    private static final JsonValidator VALIDATOR = JsonSchemaFactory.byDefault().getValidator();
    private final String prefix = "gateway/service";
    @Autowired
    private GatewayApiTextService apiTextService;
    @Autowired
    private GatewayService gwService;
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);
        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @RequiresPermissions("gateway:service:add")
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("endpointPlugins", findMatchPlugin(Constant.PLUGIN_TYPE_ENDPOINT));
        model.addAttribute("osgGatewayUrl", MultiGatewayUrlSwitcher.getGatewayUrl());
        return prefix + "/add";
    }

    @RequiresPermissions("gateway:service:import")
    @PostMapping("/checkServiceRepeat")
    @ResponseBody
    public CommonResponse checkServiceRepeat(@RequestParam(name = "serviceJsonFile") MultipartFile file) {
        try {
            if (file == null) {
                return CommonResponse.error("上传的文件为空");
            }
            String json = new String(file.getBytes());
            if (StringUtils.isBlank(json)) {
                return CommonResponse.error("上传的文件错误");
            }
            ServiceVO serviceVO = JsonUtils.fromJson(json, ServiceVO.class);
            if (!StringUtils.isBlank(serviceVO.getServiceId())) {
                if (gwService.findServiceByServiceId(serviceVO.getServiceId()) != null) {
                    return CommonResponse.error("repeat");
                }
            }
        } catch (Exception e) {
            return getCommonResponse(e);
        }
        return CommonResponse.ok();
    }

    @RequiresPermissions("gateway:service:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("endpointPlugins", findMatchPlugin(Constant.PLUGIN_TYPE_ENDPOINT));
        ServiceDTO serviceDTO = apiTextService.loadGatewayServiceById(id);
        model.addAttribute("serviceDTO", serviceDTO);
        model.addAttribute("osgGatewayUrl", MultiGatewayUrlSwitcher.getGatewayUrl());
        return prefix + "/edit";
    }

    @RequiresPermissions("gateway:service:export")
    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> export(@PathVariable("id") Long id) {
        try {
            ServiceVO serviceVO = gwService.exportService(id);
            ServiceDTO serviceDTO = new ServiceDTO();
            BeanUtils.copyProperties(serviceVO, serviceDTO);
            String fileName = serviceVO.getServiceName() + ".txt";
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "iso8859-1");// 防止中文乱码
            HttpHeaders headers = new HttpHeaders();// 设置响应头
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
            HttpStatus statusCode = HttpStatus.OK;// 设置响应吗
            ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(
                JsonUtils.formatJson(JsonUtils.serializeToJson(serviceDTO)).getBytes(), headers, statusCode);
            return response;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequiresPermissions("gateway:service:import")
    @PostMapping("/importFile")
    @ResponseBody
    public CommonResponse importFile(@RequestParam(name = "serviceJsonFile") MultipartFile file) {
        try {
            if (file == null) {
                return CommonResponse.error("上传的文件为空");
            }
            String json = new String(file.getBytes());
            if (StringUtils.isBlank(json)) {
                return CommonResponse.error("上传的文件错误");
            }
            ServiceVO serviceVO = JsonUtils.fromJson(json, ServiceVO.class);
            serviceVO.setServiceOwner(getUsername());
            gwService.saveService(serviceVO);
        } catch (Exception e) {
            return getCommonResponse(e);
        }
        return CommonResponse.ok();

    }

    @RequiresPermissions("gateway:service:list")
    @GetMapping()
    public String service(Model model) {

        model.addAttribute("select", gwService.queryServiceSelect());
        return prefix + "/list";
    }

    @GetMapping("/template/{template}")
    @ResponseBody
    public String template(@PathVariable("template") String template) {
        String path = "/META-INF/config/rules/";
        if (StringUtils.isEmpty(template)) {
            logger.error("模板名称为空");
            return "";
        }
        path = path + template.replace("--", ".");
        return FileUtil.readTextFromFile(path);
    }

    @PostMapping("/transform")
    @ResponseBody
    public CommonResponse transform(@RequestParam(name = "inputText") String inputText,
        @RequestParam(name = "freemarker") String freemarker) {
        try {
            if (!JsonUtils.isJson(inputText)) {
                return CommonResponse.error("测试输入文本不是JSON，请检查");
            }
            String transformedJson = FreemarkerMapperUtil.formatForJsonPath(inputText, freemarker);
            CommonResponse commonResponse = CommonResponse.ok();
            commonResponse.put("result", transformedJson);
            return commonResponse;
        } catch (Exception e) {
            return getCommonResponse(e);
        }
    }

    @PostMapping("/bodyvalidate")
    @ResponseBody
    public CommonResponse bodyValidate(@RequestParam(name = "inputText") String inputText,
        @RequestParam(name = "jsonschema") String jsonSchema,
        @RequestParam(name = "bodyjsonpath") String bodyJsonPath) {
        try {
            if (StringUtils.isBlank(inputText) || !JsonUtils.isJson(inputText)) {
                return CommonResponse.error("测试输入文本不是JSON，请检查");
            }
            String requestBody = inputText;
            String instance;
            if (StringUtils.isBlank(bodyJsonPath)) {
                instance = requestBody;
            } else {
                try {
                    instance = JsonPath.read(requestBody, bodyJsonPath);
                } catch (Exception e) {
                    instance = JsonUtils.serializeToJson(JsonPath.read(requestBody, bodyJsonPath));
                }
            }
            JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
            JsonNode instanceNode = JsonLoader.fromString(instance);
            ProcessingReport report = VALIDATOR.validate(schemaNode, instanceNode);
            if (!report.isSuccess()) {
                return CommonResponse.error(report.toString());
            }
            CommonResponse commonResponse = CommonResponse.ok();
            commonResponse.put("result", "校验成功");
            return commonResponse;
        } catch (Exception e) {
            return getCommonResponse(e);
        }
    }

    @GetMapping("/getServiceMap")
    @ResponseBody
    public CommonResponse getServiceMap() {
        List<Map<String, Object>> serviceMapList = gwService.queryServiceSelect();
        Map<Object, Object> resultMap = Maps.newHashMap();
        serviceMapList.forEach(service -> {
            resultMap.put(service.get("serviceId"), service.get("name"));
        });
        CommonResponse commonResponse = CommonResponse.ok();
        commonResponse.put("serviceMap", resultMap);
        return commonResponse;
    }

    private Map<String, Object> findMatchPlugin(String type) {
        String endpointPluginsStr = JsonUtils.serializeToJson(Maps.newHashMap());
        try {
            Application gatewayApp = EurekaClientUtil.getApplication(Constant.GATEWAY_APP_NAME);
            if (gatewayApp != null && gatewayApp.getInstances() != null && gatewayApp.getInstances().size() > 0) {
                InstanceInfo instanceInfo = gatewayApp.getInstances().get(0);
                String url = instanceInfo.getIPAddr() + ":" + instanceInfo.getMetadata().get("management.port");
                url = "http://" + url + "/supportplugins";
                endpointPluginsStr =
                    restTemplate.getForObject(url + "?type=" + Constant.PLUGIN_TYPE_ENDPOINT, String.class);
            } else {
                logger.warn("can not find tesla-gateway instance");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        final Map endpointPluginsMap = JsonUtils.fromJson(endpointPluginsStr, Map.class);
        FileInputStream fileInputStream = null;
        try {
            Yaml yaml = new Yaml();
            InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("support_plugins.yml");
            Map map = yaml.loadAs(resourceStream, Map.class);
            Map<String, Object> pluginsMap = (Map<String, Object>)map.get(type);
            return pluginsMap.entrySet().stream()
                // 兼容理房通的情况，请求不通gateway
                .filter(p -> CollectionUtils.isEmpty(endpointPluginsMap) || endpointPluginsMap.containsKey(p.getKey()))
                .map(e -> (Map<String, String>)e.getValue()).distinct()
                .collect(Collectors.toMap(e -> e.get("type"), e -> e.get("name")));
        } catch (Exception e) {
            logger.error("读取文件错误", e);
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return Maps.newHashMap();
    }

}
