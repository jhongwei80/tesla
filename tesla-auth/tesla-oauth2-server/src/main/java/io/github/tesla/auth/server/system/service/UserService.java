package io.github.tesla.auth.server.system.service;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.github.tesla.auth.server.system.dao.DeptDao;
import io.github.tesla.auth.server.system.dao.UserDao;
import io.github.tesla.auth.server.system.dao.UserRoleDao;
import io.github.tesla.auth.server.system.domain.DeptDO;
import io.github.tesla.auth.server.system.domain.Tree;
import io.github.tesla.auth.server.system.domain.UserDO;
import io.github.tesla.auth.server.system.domain.UserRoleDO;
import io.github.tesla.auth.server.utils.BuildTree;

@Service
public class UserService {

    @Autowired
    private UserDao userMapper;

    @Autowired
    private UserRoleDao userRoleMapper;

    @Autowired
    private DeptDao deptMapper;

    public UserDO get(Long id) {
        UserDO user = userMapper.get(id);
        if (Objects.isNull(user)) {
            return user;
        }
        List<Long> roleIds = userRoleMapper.listRoleId(id);
        user.setDeptName(deptMapper.get(user.getDeptId()).getName());
        user.setroleIds(roleIds);
        return user;
    }

    public List<UserDO> list(Map<String, Object> map) {
        List<UserDO> users = userMapper.list(map);
        if (CollectionUtils.isEmpty(users)) {
            return users;
        }
        users.forEach(user -> {
            DeptDO dept = deptMapper.get(user.getDeptId());
            user.setDeptName(Objects.isNull(dept) ? null : dept.getName());
        });
        return users;
    }

    public int count(Map<String, Object> map) {
        return userMapper.count(map);
    }

    @Transactional(rollbackFor = Exception.class)

    public boolean save(UserDO user) {
        user.setGmtCreate(new Date());
        user.setGmtModified(user.getGmtCreate());
        if (userMapper.save(user) != 1) {
            return false;
        }
        Long userId = user.getUserId();
        List<Long> roles = user.getroleIds();
        userRoleMapper.removeByUserId(userId);
        this.batchSaveRole(userId, roles);
        return true;
    }

    public boolean update(UserDO user) {
        if (userMapper.update(user) != 1) {
            return false;
        }
        Long userId = user.getUserId();
        List<Long> roles = user.getroleIds();
        userRoleMapper.removeByUserId(userId);
        this.batchSaveRole(userId, roles);
        return true;
    }

    private boolean batchSaveRole(Long userId, List<Long> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }
        List<UserRoleDO> list = new ArrayList<>();
        for (Long roleId : roles) {
            UserRoleDO ur = new UserRoleDO();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        if (list.size() > 0) {
            userRoleMapper.batchSave(list);
        }
        return true;
    }

    public boolean remove(Long userId) {
        userRoleMapper.removeByUserId(userId);
        return userMapper.remove(userId) == 1;
    }

    public boolean exit(Map<String, Object> params) {
        return userMapper.list(params).size() > 0;
    }

    public Set<String> listRoles(Long userId) {
        return null;
    }

    public boolean resetPwd(UserDO user, String newPasswd) {
        UserDO condition = new UserDO();
        condition.setUserId(user.getUserId());
        condition.setPassword(newPasswd);
        condition.setGmtModified(new Date());
        return userMapper.update(condition) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchremove(Long[] userIds) {
        userRoleMapper.batchRemoveByUserId(userIds);
        return userMapper.batchRemove(userIds) > 0;
    }

    public Tree<DeptDO> getTree() {
        List<Tree<DeptDO>> trees = new ArrayList<Tree<DeptDO>>();
        List<DeptDO> depts = deptMapper.list(new HashMap<String, Object>(16));
        Long[] pDepts = deptMapper.listParentDept();
        Long[] uDepts = userMapper.listAllDept();
        Long[] allDepts = ArrayUtils.addAll(pDepts, uDepts);
        for (DeptDO dept : depts) {
            if (!ArrayUtils.contains(allDepts, dept.getDeptId())) {
                continue;
            }
            Tree<DeptDO> tree = new Tree<DeptDO>();
            tree.setId(dept.getDeptId().toString());
            tree.setParentId(dept.getParentId().toString());
            tree.setText(dept.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            state.put("mType", "dept");
            tree.setState(state);
            trees.add(tree);
        }
        List<UserDO> users = userMapper.list(new HashMap<String, Object>(16));
        for (UserDO user : users) {
            Tree<DeptDO> tree = new Tree<DeptDO>();
            tree.setId(user.getUserId().toString());
            tree.setParentId(user.getDeptId().toString());
            tree.setText(user.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            state.put("mType", "user");
            tree.setState(state);
            trees.add(tree);
        }
        return BuildTree.build(trees);
    }

}
