package io.github.tesla.ops.gwservice.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Maps;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.gwservice.service.GatewayService;
import io.github.tesla.ops.gwservice.vo.GatewayServiceVo;
import io.github.tesla.ops.gwservice.vo.ServiceVO;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @author: zhangzhiping
 * @date: 2018/12/6 17:12
 * @description:
 */
@RestController
@RequestMapping("/api/gateway/service")
public class GwServiceController extends BaseController {

    @Autowired
    private GatewayService gwService;

    @Log("删除服务配置")
    @RequiresPermissions("gateway:service:remove")
    @DeleteMapping("/{id}")
    public CommonResponse del(@PathVariable("id") Long id) {
        try {
            gwService.deleteService(id);
        } catch (Exception e) {
            return getCommonResponse(e);
        }
        return CommonResponse.ok();
    }

    @Log("查询服务配置列表")
    @RequiresPermissions("gateway:service:list")
    @GetMapping
    public PageDO<GatewayServiceVo> list(@RequestParam Map<String, Object> params) {
        return gwService.queryList(new Query(params));
    }

    @Log("审批服务配置")
    @RequiresPermissions("gateway:service:edit")
    @PutMapping("/{id}")
    public CommonResponse review(@PathVariable("id") Long id) {
        try {
            return gwService.review(id) ? CommonResponse.ok() : CommonResponse.error("审核失败,未找到对应的API配置");
        } catch (Exception e) {
            return getCommonResponse(e);
        }
    }

    @Log("保存服务配置")
    @RequiresPermissions("gateway:service:add")
    @PostMapping
    public CommonResponse save(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            ServiceVO serviceVO = JsonUtils.fromJson(multipartRequest.getParameter("serviceDTO"), ServiceVO.class);
            if (!MapUtils.isEmpty(multipartRequest.getMultiFileMap())) {
                Map<String, byte[]> fileMap = Maps.newHashMap();
                for (String fileKey : multipartRequest.getMultiFileMap().keySet()) {
                    fileMap.put(fileKey, multipartRequest.getMultiFileMap().get(fileKey).get(0).getBytes());
                }
                PluginDefinition.uploadFileMap.set(fileMap);
            }
            serviceVO.setServiceOwner(getUsername());
            gwService.saveService(serviceVO);
        } catch (Exception e) {
            return getCommonResponse(e);
        } finally {
            PluginDefinition.uploadFileMap.remove();
        }
        return CommonResponse.ok();
    }

}
