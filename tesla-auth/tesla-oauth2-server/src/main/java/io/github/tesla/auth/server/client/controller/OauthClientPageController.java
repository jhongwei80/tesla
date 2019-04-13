package io.github.tesla.auth.server.client.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.tesla.auth.server.client.service.OauthClientService;
import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.oauth.domian.oauth.ClientDetails;

@Controller
@RequestMapping("/oauth2/client")
public class OauthClientPageController extends BaseController {
    private String prefix = "oauth/client";
    @Autowired
    private OauthClientService oauthClientService;

    @RequiresPermissions("oauth:client:list")
    @GetMapping("list")
    public String list(Model model) {

        return prefix + "/list";
    }

    @RequiresPermissions("oauth:client:add")
    @GetMapping("/add")
    public String add(Model model) {

        return prefix + "/add";
    }

    @GetMapping("/edit/{clientId}")
    @RequiresPermissions("oauth:client:edit")
    public String edit(Model model, @PathVariable("clientId") String clientId) {
        ClientDetails clientDetails = oauthClientService.get(clientId);
        model.addAttribute("clientDetail", clientDetails);
        return prefix + "/edit";
    }

}
