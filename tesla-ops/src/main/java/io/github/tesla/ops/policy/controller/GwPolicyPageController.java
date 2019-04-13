package io.github.tesla.ops.policy.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.policy.service.GwPolicyService;
import io.github.tesla.ops.policy.vo.GwPolicyVo;

/**
 * @ClassName GwPolicyPageController
 * @Description Forward action implementation for gw policy controller
 * @Author zhouchao
 * @Date 2018/12/6 18:08
 * @Version 1.0
 **/
@Controller
@RequestMapping("gateway/policy")
public class GwPolicyPageController extends BaseController {

    private final String prefix = "gateway/policy";

    @Autowired
    private GwPolicyService gwPolicyService;

    @RequiresPermissions("gateway:policy:add")
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("appControlSelect", gwPolicyService.getAppControl());
        return prefix + "/add";
    }

    @RequiresPermissions("gateway:policy:add")
    @GetMapping("/copy/{id}")
    @ResponseBody
    public CommonResponse copy(@PathVariable("id") Long id) {
        gwPolicyService.copyById(id);
        return CommonResponse.ok();
    }

    @RequiresPermissions("gateway:policy:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        GwPolicyVo gwPolicyVo = gwPolicyService.getGwPolicyById2Edit(id);
        model.addAttribute("policyVo", gwPolicyVo);
        model.addAttribute("appControlSelect", gwPolicyService.getAppControl());
        return prefix + "/edit";
    }

    @RequiresPermissions("gateway:policy:list")
    @GetMapping()
    public String serivce(Model model) {
        return prefix + "/list";
    }

}
