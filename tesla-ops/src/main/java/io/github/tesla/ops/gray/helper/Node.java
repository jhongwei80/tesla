package io.github.tesla.ops.gray.helper;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/11/9 10:53
 * @version: V1.0.0
 * @since JDK 11
 */
public class Node {

    private String id;

    /**
     * 节点标签
     */
    private String label;

    /**
     * 节点级别
     */
    private int level;

    private int group;

    /**
     * 条件
     */
    private List<GrayPolicyConditionDO> conditions = Lists.newArrayList();

    /**
     * 是否父节点
     */
    private boolean parent;

    /**
     * 是否灰度节点
     */
    private boolean grayNode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node)o;
        return Objects.equals(id, node.id);
    }

    public List<GrayPolicyConditionDO> getConditions() {
        return conditions;
    }

    public int getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isGrayNode() {
        return grayNode;
    }

    public boolean isParent() {
        return parent;
    }

    public void setConditions(List<GrayPolicyConditionDO> conditions) {
        this.conditions = conditions;
    }

    public void setGrayNode(boolean grayNode) {
        this.grayNode = grayNode;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }
}
