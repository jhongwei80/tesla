package io.github.tesla.ops.gray.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.gray.helper.Edges;
import io.github.tesla.ops.gray.helper.GrayParamKind;
import io.github.tesla.ops.gray.helper.Node;
import io.github.tesla.ops.gray.helper.ServiceTarget;
import io.github.tesla.ops.gray.service.GrayService;
import io.github.tesla.ops.gray.vo.GrayPlanVO;

@Controller
@RequestMapping("gray")
public class GrayPlanPageController extends BaseController {

    private static final String PREFIX = "gray/";

    @Autowired
    private GrayService grayService;

    @RequiresPermissions("gray:add")
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("consumers", GrayParamKind.get(ServiceTarget.CONSUMER));
        model.addAttribute("providers", GrayParamKind.get(ServiceTarget.PROVIDER));
        return PREFIX + "add";
    }

    @RequiresPermissions("gray:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("consumers", GrayParamKind.get(ServiceTarget.CONSUMER));
        model.addAttribute("providers", GrayParamKind.get(ServiceTarget.PROVIDER));

        GrayPlanVO grayPlanVO = grayService.getGrayPlanVOFormDB(id);
        model.addAttribute("grayPlan", grayPlanVO);
        return PREFIX + "edit";
    }

    @RequiresPermissions("gray:list")
    @GetMapping()
    public String list() {
        return PREFIX + "gray";
    }

    @RequiresPermissions("gray:list")
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        Pair<Set<Node>, List<Edges>> nodes = grayService.findNodes(id);
        Map<String, Object> result = Maps.newHashMap();
        result.put("nodes", nodes.getLeft());
        result.put("edges", nodes.getRight());
        model.addAttribute("nodes", result);
        return PREFIX + "view";
    }

}
