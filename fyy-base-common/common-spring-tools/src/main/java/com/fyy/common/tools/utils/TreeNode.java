package com.fyy.common.tools.utils;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点，所有需要实现树节点的，都需要继承该类
 *
 * @author fyy
 * @since 2023/02/28
 */
public class TreeNode<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 上级ID
     */
    private Long pid;

    private Integer level;

    @ApiModelProperty(value = "数据类型，DIRECTORY:目录，WITH_MODULE_DATA:queryType指定的对应模块的数据；在树形列表查询的情况下，name和id实际含义由该参数确定，若为目录则是目录名称及目录的id，若是实际的数据，name和id则为对应模块的数据，默认情况下是目录")
    private String dataType = "DIRECTORY";
    /**
     * 子节点列表
     */
    private List<T> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}