package io.github.tesla.ops.policy.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.policy.service.GwPolicyService;
import io.github.tesla.ops.policy.vo.GwPolicyVo;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @ClassName GwPolicyController
 * @Description CRUD action implementation for gw policy controller
 * @Author zhouchao
 * @Date 2018/12/6 18:07
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/gateway/policy")
public class GwPolicyController extends BaseController {

    @Autowired
    private GwPolicyService gwPolicyService;

    @Autowired
    private GatewayApiTextService apiTextService;

    @RequiresPermissions("gateway:policy:batchRemove")
    @Log("批量Policy配置")
    @DeleteMapping
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        if (!Objects.isNull(ids)) {
            Arrays.stream(ids).forEach((id) -> {
                gwPolicyService.removeGwPolicyVo(id);
            });
        }
        return CommonResponse.ok();
    }

    @Log("删除Policy配置")
    @RequiresPermissions("gateway:policy:remove")
    @DeleteMapping("/{id}")
    public CommonResponse del(@PathVariable("id") Long id) {
        gwPolicyService.removeGwPolicyVo(id);
        return CommonResponse.ok();
    }

    @Log("查询Policy配置列表")
    @RequiresPermissions("gateway:policy:list")
    @GetMapping
    public PageDO<GwPolicyVo> list(@RequestParam Map<String, Object> params) {
        return gwPolicyService.queryList(new Query(params));
    }

    @Log("保存Policy配置")
    @RequiresPermissions("gateway:policy:add")
    @PostMapping
    public CommonResponse save(GwPolicyVo gwPolicyVo) {
        gwPolicyService.saveGwPolicyVo(gwPolicyVo);
        return CommonResponse.ok();
    }

    @Log("更新Policy配置")
    @RequiresPermissions("gateway:policy:edit")
    @PutMapping
    public CommonResponse update(GwPolicyVo gwPolicyVo) {
        gwPolicyService.updateGwPolicyVo(gwPolicyVo);
        return CommonResponse.ok();
    }

}
