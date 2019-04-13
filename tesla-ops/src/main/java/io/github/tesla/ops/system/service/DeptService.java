package io.github.tesla.ops.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import io.github.tesla.ops.system.dao.DeptDao;
import io.github.tesla.ops.system.domain.DeptDO;
import io.github.tesla.ops.system.domain.Tree;
import io.github.tesla.ops.utils.BuildTree;

@Service
public class DeptService {

    @Autowired
    private DeptDao sysDeptMapper;

    public boolean batchRemove(Long[] deptIds) {
        return sysDeptMapper.batchRemove(deptIds) > 0;
    }

    public boolean checkDeptHasUser(Long deptId) {
        // 查询部门以及此部门的下级部门
        int result = sysDeptMapper.getDeptUserNumber(deptId);
        return result == 0;
    }

    public int count(Map<String, Object> map) {
        return sysDeptMapper.count(map);
    }

    public DeptDO get(Long deptId) {
        return sysDeptMapper.get(deptId);
    }

    public Tree<DeptDO> getTree() {
        List<Tree<DeptDO>> trees = new ArrayList<Tree<DeptDO>>();
        List<DeptDO> sysDepts = sysDeptMapper.list(Maps.newHashMap());
        for (DeptDO sysDept : sysDepts) {
            Tree<DeptDO> tree = new Tree<DeptDO>();
            tree.setId(sysDept.getDeptId().toString());
            tree.setParentId(sysDept.getParentId().toString());
            tree.setText(sysDept.getName());
            Map<String, Object> state = new HashMap<>(16);
            state.put("opened", true);
            tree.setState(state);
            trees.add(tree);
        }
        // 默认顶级菜单为0，根据数据库实际情况调整
        return BuildTree.build(trees);
    }

    public List<DeptDO> list(Map<String, Object> map) {
        return sysDeptMapper.list(map);
    }

    public boolean remove(Long deptId) {
        return sysDeptMapper.remove(deptId) == 1;
    }

    public boolean save(DeptDO sysDept) {
        return sysDeptMapper.save(sysDept) == 1;
    }

    public boolean update(DeptDO sysDept) {
        return sysDeptMapper.update(sysDept) == 1;
    }

}
