package io.github.tesla.ops.system.controller;

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

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.common.MultiDataSourceSwitcher;
import io.github.tesla.ops.system.domain.MenuDO;
import io.github.tesla.ops.system.domain.Tree;
import io.github.tesla.ops.system.service.MenuService;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/403")
    public String error403() {
        return "403";
    }

    @Log("请求访问主页")
    @GetMapping({"/index"})
    public String index(Model model) {
        List<Tree<MenuDO>> menus = menuService.listMenuTree(getUserId());
        model.addAttribute("menus", menus);
        model.addAttribute("username",
            MultiDataSourceSwitcher.getMultiDataSourceNames()
                .get(SecurityUtils.getSubject().getSession().getAttribute("datasource")) + "--"
                + BaseController.getUsername());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("multiDataSourceNames", MultiDataSourceSwitcher.getMultiDataSourceNames());
        return "login";
    }

    @Log("登录")
    @PostMapping("/login")
    @ResponseBody
    public CommonResponse login(String username, String password, String datasource) {
        MultiDataSourceSwitcher.setDataSourceKey(datasource);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            subject.getSession().setAttribute("datasource", datasource);
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

    @GetMapping({"/", ""})
    public String welcome() {
        return "redirect:/index";
    }

}
