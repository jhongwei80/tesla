package io.github.tesla.ops.gray.helper;

import java.util.List;

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
public class Edges {

    private String from;

    private String to;

    /**
     * 条件
     */
    private List<GrayPolicyConditionDO> conditions = Lists.newArrayList();

    private String label;

    /**
     * 脚本
     */
    private String groovy;

    public List<GrayPolicyConditionDO> getConditions() {
        return conditions;
    }

    public String getFrom() {
        return from;
    }

    public String getGroovy() {
        return groovy;
    }

    public String getLabel() {
        return label;
    }

    public String getTo() {
        return to;
    }

    public void setConditions(List<GrayPolicyConditionDO> conditions) {
        this.conditions = conditions;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setGroovy(String groovy) {
        this.groovy = groovy;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
