package io.github.tesla.ops.gray.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.BaseResult;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.gray.dao.GrayPlanRepository;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.helper.BeanMapper;
import io.github.tesla.ops.gray.helper.YesNoKind;
import io.github.tesla.ops.gray.service.GrayService;
import io.github.tesla.ops.gray.vo.GrayPlanVO;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

@RestController
@RequestMapping("/api/gray")
public class GrayPlanController extends BaseController {

    @Autowired
    private GrayService grayService;

    @Autowired
    private GrayPlanRepository grayPlanRepository;

    @RequiresPermissions("gray:add")
    @Log("新增计划")
    @PostMapping()
    public CommonResponse add(GrayPlanVO grayPlan) {

        CommonResponse commonResponse = null;
        BaseResult result = grayService.createGrayPlan(grayPlan);
        if (result.isSuccess()) {
            commonResponse = CommonResponse.ok("id", grayPlan.getId());
            commonResponse.put("enable", grayPlan.getEnable());
            commonResponse.putAll(result.getData());
        } else {
            commonResponse = CommonResponse.error(result.getReturnMessage());
        }

        return commonResponse;
    }

    @RequiresPermissions("gray:batchRemove")
    @Log("批量删除计划")
    @DeleteMapping
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        for (Long id : ids) {
            grayService.deleteGrayCascade(id);
        }
        return CommonResponse.ok();
    }

    @RequiresPermissions("gray:remove")
    @Log("删除计划")
    @DeleteMapping("/{id}")
    public CommonResponse delete(@PathVariable(name = "id") Long id) {
        BaseResult result = grayService.deleteGrayCascade(id);
        return result.isSuccess() ? CommonResponse.ok() : CommonResponse.error(result.getReturnMessage());
    }

    @RequiresPermissions("gray:remove")
    @Log("禁用灰度计划")
    @GetMapping("/disable/{id}")
    public CommonResponse disable(@PathVariable("id") Long id) {
        return grayPlanRepository.updateEnbale(id, YesNoKind.NO, YesNoKind.YES) ? CommonResponse.ok()
            : CommonResponse.error();
    }

    @GetMapping("/existed/{name}")
    public CommonResponse existed(@PathVariable("name") String name) {
        boolean existed = grayPlanRepository.existed(name);
        return CommonResponse.ok("existed", existed);
    }

    @RequiresPermissions("gray:list")
    @GetMapping()
    public PageDO<GrayPlanDO> list(@RequestParam Map<String, Object> params) {
        return grayPlanRepository.findByPage(new Query(params));
    }

    @RequiresPermissions("gray:edit")
    @Log("推送灰度策略")
    @GetMapping("/push/{id}")
    public CommonResponse pushGray(@PathVariable(name = "id") Long id) {
        BaseResult result = grayService.pushGrayPlan(id);
        return result.isSuccess() ? CommonResponse.ok() : CommonResponse.error(result.getReturnMessage());
    }

    @RequiresPermissions("gray:edit")
    @Log("更新计划")
    @PutMapping()
    public CommonResponse update(@RequestBody GrayPlanVO grayPlan) {
        GrayPlanDO grayPlanDO = BeanMapper.map(grayPlan, GrayPlanDO.class);
        boolean result = grayPlanRepository.update(grayPlanDO);
        return result ? CommonResponse.ok() : CommonResponse.error();
    }
}
