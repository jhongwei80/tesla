package io.github.tesla.ops.system.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Tree<T> {
    /**
     * 节点ID
     */
    private String id;
    /**
     * 显示节点文本
     */
    private String text;
    /**
     * 节点状态，open closed
     */
    private Map<String, Object> state;
    /**
     * 节点是否被选中 true false
     */
    private boolean checked = false;
    /**
     * 节点属性
     */
    private Map<String, Object> attributes;

    /**
     * 节点的子节点
     */
    private List<Tree<T>> children = new ArrayList<Tree<T>>();

    /**
     * 父ID
     */
    private String parentId;
    /**
     * 是否有父节点
     */
    private boolean hasParent = false;
    /**
     * 是否有子节点
     */
    private boolean hasChildren = false;

    public Tree() {
        super();
    }

    public Tree(String id, String text, Map<String, Object> state, boolean checked, Map<String, Object> attributes,
        List<Tree<T>> children, boolean isParent, boolean isChildren, String parentID) {
        super();
        this.id = id;
        this.text = text;
        this.state = state;
        this.checked = checked;
        this.attributes = attributes;
        this.children = children;
        this.hasParent = isParent;
        this.hasChildren = isChildren;
        this.parentId = parentID;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public String getText() {
        return text;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public boolean isHasParent() {
        return hasParent;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setChildren(boolean isChildren) {
        this.hasChildren = isChildren;
    }

    public void setChildren(List<Tree<T>> children) {
        this.children = children;
    }

    public void setHasParent(boolean isParent) {
        this.hasParent = isParent;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {

        return JSON.toJSONString(this);
    }

}
