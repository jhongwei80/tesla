package io.github.tesla.auth.server.system.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.CommonResponse;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.system.domain.MenuDO;
import io.github.tesla.auth.server.system.domain.Tree;
import io.github.tesla.auth.server.system.service.MenuService;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private MenuService menuService;

    @GetMapping({"/", ""})
    public String welcome() {
        return "redirect:/index";
    }

    @Log("请求访问主页")
    @GetMapping({"/index"})
    public String index(Model model) {
        List<Tree<MenuDO>> menus = menuService.listMenuTree(getUserId());
        model.addAttribute("menus", menus);
        model.addAttribute("username", BaseController.getUsername());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @Log("登录")
    @PostMapping("/login")
    @ResponseBody
    public CommonResponse login(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            return CommonResponse.ok();
        } catch (AuthenticationException e) {
            throw e;
        }
    }

    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";
    }

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }

}
