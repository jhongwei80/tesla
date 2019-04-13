package io.github.tesla.ops.gray.controller;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.BaseResult;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.gray.dao.GrayPlanRepository;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.dto.GrayPolicyDTO;
import io.github.tesla.ops.gray.helper.BeanMapper;
import io.github.tesla.ops.gray.service.GrayService;
import io.github.tesla.ops.gray.vo.GrayPlanVO;
import io.github.tesla.ops.gray.vo.GrayPolicyConditionVO;
import io.github.tesla.ops.gray.vo.GrayPolicyVO;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

@RestController
@RequestMapping("/api/gray/policy")
public class GrayPolicyController extends BaseController {

    @Autowired
    private GrayService grayService;

    @Autowired
    private GrayPlanRepository grayPlanRepository;

    @RequiresPermissions("gray:add")
    @Log("新增计划")
    @PostMapping()
    public CommonResponse add(GrayPolicyVO form) {
        if (form.getPlanId() == null) {
            return CommonResponse.error("Please check gray plan parameter.");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(form.getConsumerService())
            || org.apache.commons.lang3.StringUtils.isEmpty(form.getProviderService())) {
            return CommonResponse.error("Please select service.");
        }
        if (org.apache.commons.lang3.StringUtils.equals(form.getConsumerService(), form.getProviderService())) {
            return CommonResponse.error("The consumer and provider service not the same.");
        }
        if (CollectionUtils.isEmpty(form.getGrayPolicyParamConditionVOS())
            || CollectionUtils.isEmpty(form.getGrayPolicyNodeConditionVOS())) {
            return CommonResponse.error("The check gray policy condition.");
        }
        GrayPolicyDTO grayPolicy = new GrayPolicyDTO();
        grayPolicy.setGrayPolicy(BeanMapper.map(form, GrayPolicyDO.class));
        grayPolicy.addCondition(BeanMapper.mapList(form.getGrayPolicyParamConditionVOS(), GrayPolicyConditionVO.class,
            GrayPolicyConditionDO.class));
        grayPolicy.addCondition(BeanMapper.mapList(form.getGrayPolicyNodeConditionVOS(), GrayPolicyConditionVO.class,
            GrayPolicyConditionDO.class));

        BaseResult result = grayService.addGrayPolicy(form.getPlanId(), grayPolicy);
        return result.isSuccess() ? CommonResponse.ok() : CommonResponse.error(result.getReturnMessage());
    }

    @RequiresPermissions("gray:remove")
    @Log("删除策略")
    @DeleteMapping("/{id}")
    public CommonResponse delete(@PathVariable(name = "id") Long id) {
        BaseResult baseResult = grayService.deletPolicy(id);
        return baseResult.isSuccess() ? CommonResponse.ok() : CommonResponse.error();
    }

    @RequiresPermissions("gray:list")
    @GetMapping()
    public PageDO<GrayPlanDO> list(@RequestParam Map<String, Object> params) {
        return grayPlanRepository.findByPage(new Query(params));
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
