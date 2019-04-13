package io.github.tesla.ops.waf.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;
import io.github.tesla.ops.waf.service.GwWafService;
import io.github.tesla.ops.waf.vo.GwWafVo;

/**
 * @ClassName GwWafController
 * @Description CRUD action implementation for gw policy controller
 * @Author zhouchao
 * @Date 2018/12/13 15:31
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/gateway/waf")
public class GwWafController extends BaseController {

    @Autowired
    private GwWafService gwWafService;

    @Log("查询Policy配置列表")
    @RequiresPermissions("gateway:waf:list")
    @GetMapping
    public PageDO<GwWafVo> list(@RequestParam Map<String, Object> params) {
        return gwWafService.queryList(new Query(params));
    }

    @Log("更新Policy配置")
    @RequiresPermissions("gateway:waf:edit")
    @PutMapping
    public CommonResponse update(GwWafVo gwWafVo) {
        gwWafService.updateGwWaf(gwWafVo);
        return CommonResponse.ok();
    }

}
