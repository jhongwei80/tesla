package io.github.tesla.ops.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.tesla.ops.common.BaseController;

@Controller
public class IndexPageController extends BaseController {

    @GetMapping("/index/introduction")
    public String introduction() {
        return "osg_introduction";
    }

}
