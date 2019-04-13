package io.github.tesla.ops.waf.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.waf.service.GwWafService;
import io.github.tesla.ops.waf.vo.GwWafVo;

/**
 * @ClassName GwWafPageController
 * @Description Forward action implementation for gw policy controller
 * @Author zhouchao
 * @Date 2018/12/13 15:31
 * @Version 1.0
 **/
@Controller
@RequestMapping("gateway/waf")
public class GwWafPageController extends BaseController {

    private final String prefix = "gateway/waf";

    @Autowired
    private GwWafService gwWafService;

    @RequiresPermissions("gateway:waf:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        GwWafVo gwWafVo = gwWafService.getGwWafById2Edit(id);
        model.addAttribute("wafVo", gwWafVo);
        model.addAttribute("appControlOptions", gwWafService.getAppControl());
        return prefix + "/edit";
    }

    @RequiresPermissions("gateway:waf:list")
    @GetMapping()
    public String serivce(Model model) {
        return prefix + "/list";
    }

}
