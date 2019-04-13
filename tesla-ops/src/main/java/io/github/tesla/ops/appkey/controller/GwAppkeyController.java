package io.github.tesla.ops.appkey.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.filter.utils.UUIDUtil;
import io.github.tesla.ops.appkey.service.GwAppkeyService;
import io.github.tesla.ops.appkey.vo.GwAppkeyVo;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @ClassName GwAppkeyController
 * @Description CRUD action implementation for gw app-key controller
 * @Author zhouchao
 * @Date 2018/12/11 13:24
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/gateway/appkey")
public class GwAppkeyController extends BaseController {

    @Autowired
    private GwAppkeyService gwAppkeyService;

    @Log("保存AppKey配置")
    @RequiresPermissions("gateway:appkey:add")
    @PostMapping
    public CommonResponse save(GwAppkeyVo gwAppkeyVo) {
        gwAppkeyService.saveGwAppkey(gwAppkeyVo);
        return CommonResponse.ok();
    }

    @Log("查询AppKey配置列表")
    @RequiresPermissions("gateway:appkey:list")
    @GetMapping
    public PageDO<GwAppkeyVo> list(@RequestParam Map<String, Object> params) {
        return gwAppkeyService.queryList(new Query(params));
    }

    @Log("更新AppKey配置")
    @RequiresPermissions("gateway:appkey:edit")
    @PutMapping
    public CommonResponse update(GwAppkeyVo gwAppkeyVo) {
        gwAppkeyService.saveGwAppkey(gwAppkeyVo);
        return CommonResponse.ok();
    }

    @Log("删除AppKey配置")
    @RequiresPermissions("gateway:appkey:remove")
    @DeleteMapping("/{id}")
    public CommonResponse del(@PathVariable("id") Long id) {
        gwAppkeyService.removeGwAppkey(id);
        return CommonResponse.ok();
    }

    @RequiresPermissions("gateway:appkey:batchRemove")
    @Log("批量AppKey配置")
    @DeleteMapping
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        if (!Objects.isNull(ids)) {
            Arrays.stream(ids).forEach((id) -> {
                gwAppkeyService.removeGwAppkey(id);
            });
        }
        return CommonResponse.ok();
    }

    @Log("随机生成AppKey")
    @RequiresPermissions("gateway:appkey:generate")
    @GetMapping("/generateKey")
    public CommonResponse generateAppKey() {
        String accessKey = UUIDUtil.randomSecret();
        return CommonResponse.ok(accessKey);
    }

}
