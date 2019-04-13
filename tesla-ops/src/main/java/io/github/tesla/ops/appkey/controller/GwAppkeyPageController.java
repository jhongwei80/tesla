package io.github.tesla.ops.appkey.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.tesla.ops.appkey.service.GwAppkeyService;
import io.github.tesla.ops.appkey.vo.GwAppkeyVo;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;

/**
 * @ClassName GwAppkeyPageController
 * @Description Forward action implementation for gw app-key controller
 * @Author zhouchao
 * @Date 2018/12/11 13:24
 * @Version 1.0
 **/
@Controller
@RequestMapping("gateway/appkey")
public class GwAppkeyPageController extends BaseController {

    private final String prefix = "gateway/appkey";

    @Autowired
    private GwAppkeyService gwAppkeyService;

    @RequiresPermissions("gateway:appkey:add")
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("appControlOptions", gwAppkeyService.getAppControl());
        model.addAttribute("policyOptions", gwAppkeyService.getPolicyInfo());
        return prefix + "/add";
    }

    @RequiresPermissions("gateway:appkey:add")
    @GetMapping("/copy/{id}")
    @ResponseBody
    public CommonResponse copy(@PathVariable("id") Long id) {
        gwAppkeyService.copyById(id);
        return CommonResponse.ok();
    }

    @RequiresPermissions("gateway:appkey:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        GwAppkeyVo gwAppkeyVo = gwAppkeyService.getGwPolicyById2Edit(id);
        model.addAttribute("appKeyVo", gwAppkeyVo);
        model.addAttribute("policyOptions", gwAppkeyService.getPolicyInfo());
        model.addAttribute("appControlOptions", gwAppkeyService.getAppControl());
        return prefix + "/edit";
    }

    @GetMapping("/getAppKeyMap")
    @ResponseBody
    public CommonResponse getAppKeyMap() {
        Map<String, String> appKeyMap = gwAppkeyService.getAppKeyMap();
        CommonResponse commonResponse = CommonResponse.ok();
        commonResponse.put("appKeyMap", appKeyMap);
        return commonResponse;
    }

    @RequiresPermissions("gateway:appkey:list")
    @GetMapping()
    public String serivce(Model model) {
        return prefix + "/list";
    }

}
