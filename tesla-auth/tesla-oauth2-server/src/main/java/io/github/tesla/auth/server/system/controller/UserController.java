package io.github.tesla.auth.server.system.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.CommonResponse;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.common.Pageable;
import io.github.tesla.auth.server.system.domain.DeptDO;
import io.github.tesla.auth.server.system.domain.Tree;
import io.github.tesla.auth.server.system.domain.UserDO;
import io.github.tesla.auth.server.system.service.UserService;
import io.github.tesla.auth.server.system.vo.PasswdVo;
import io.github.tesla.auth.server.utils.MD5Utils;
import io.github.tesla.auth.server.utils.Query;

@RestController
@RequestMapping("/api/sys/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Pageable list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<UserDO> sysUserList = userService.list(query);
        int total = userService.count(query);
        return new Pageable(sysUserList, total);
    }

    @Log("保存用户")
    @RequiresPermissions("sys:user:add")
    @PostMapping
    public CommonResponse save(UserDO user) {
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        user.setUserIdCreate(getUserId());
        return userService.save(user) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("更新用户")
    @RequiresPermissions("sys:user:edit")
    @PutMapping
    public CommonResponse update(UserDO user) {
        return userService.update(user) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("删除用户")
    @RequiresPermissions("sys:user:remove")
    @DeleteMapping("/{id}")
    public CommonResponse remove(@PathVariable("id") Long id) {
        return userService.remove(id) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("批量删除用户")
    @RequiresPermissions("sys:user:batchRemove")
    @DeleteMapping
    CommonResponse batchRemove(@RequestParam("ids[]") Long[] userIds) {
        return userService.batchremove(userIds) ? CommonResponse.ok() : CommonResponse.error();
    }

    @PostMapping("/exit")
    public boolean exit(@RequestParam Map<String, Object> params) {
        String flag = (String)params.get("flag");
        if ("add".equalsIgnoreCase(flag)) {
            // 存在，不通过，false
            return !userService.exit(params);
        } else if ("edit".equalsIgnoreCase(flag)) {
            Long userId = Long.parseLong((String)params.get("userId"));
            params.remove("userId");
            List<UserDO> users = userService.list(params);
            if (CollectionUtils.isEmpty(users)) {
                return true;
            }
            for (UserDO user : users) {
                if (user.getUserId().compareTo(userId) != 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Log("提交更改用户密码")
    @PostMapping("/resetPwd")
    public CommonResponse resetPwd(PasswdVo form) {
        UserDO userDO = userService.get(form.getUserId());
        if (Objects.isNull(userDO)) {
            return CommonResponse.error("用户不存在");
        }
        if (!StringUtils.hasText(form.getNewPasswd()) || !StringUtils.hasText(form.getConfirmPasswd())) {
            return CommonResponse.error("请输入新密码");
        }
        String nPasswd = MD5Utils.encrypt(userDO.getUsername(), form.getNewPasswd());
        String cPasswd = MD5Utils.encrypt(userDO.getUsername(), form.getConfirmPasswd());
        if (!nPasswd.equals(cPasswd)) {
            return CommonResponse.error("新密码和确认密码不一致");
        }
        return userService.resetPwd(userDO, nPasswd) ? CommonResponse.ok() : CommonResponse.error();
    }

    @GetMapping("/tree")
    public Tree<DeptDO> tree() {
        return userService.getTree();
    }

}
