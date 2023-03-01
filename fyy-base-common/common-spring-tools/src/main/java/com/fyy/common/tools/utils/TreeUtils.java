package com.fyy.common.tools.utils;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形结构工具类，如：菜单、部门等
 *
 * @author carl
 * @since 1.0.0
 */
public class TreeUtils {

    /**
     * 根目录
     */
    public static final Long ROOT = 0L;

    public static final String ID_PATH_SEPARATOR = "/";


    /**
     * 根据pid，构建树节点
     */
    public static <T extends TreeNode> List<T> build(List<T> treeNodes, Long pid) {
        //pid不能为空
        AssertUtils.isNull(pid, "pid");

        List<T> treeList = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (pid.equals(treeNode.getPid())) {
                treeList.add(findChildren(treeNodes, treeNode));
            }
        }

        return treeList;
    }

    /**
     * 查找子节点
     */
    private static <T extends TreeNode> T findChildren(List<T> treeNodes, T rootNode) {
        for (T treeNode : treeNodes) {
            if (rootNode.getId().equals(treeNode.getPid())) {
                rootNode.getChildren().add(findChildren(treeNodes, treeNode));
            }
        }
        return rootNode;
    }

    /**
     * 构建树节点
     */
    public static <T extends TreeNode> List<T> build(List<T> treeNodes) {
        List<T> result = new ArrayList<>();

        //list转map
        Map<Long, T> nodeMap = new LinkedHashMap<>(treeNodes.size());
        for (T treeNode : treeNodes) {
            nodeMap.put(treeNode.getId(), treeNode);
        }

        for (T node : nodeMap.values()) {
            T parent = nodeMap.get(node.getPid());
            if (parent != null && !(node.getId().equals(parent.getId()))) {
                parent.getChildren().add(node);
                continue;
            }

            result.add(node);
        }

        return result;
    }


    /**
     * 构建树节点
     */
    public static <T extends TreeNode> List<T> buildWithDirectoryTree(List<T> treeNodes) {
        List<T> result = new ArrayList<>();

        //list转map
        Map<String, T> nodeMap = new LinkedHashMap<>(treeNodes.size());
        for (T treeNode : treeNodes) {
            nodeMap.put(getTreeIdWithDataType(treeNode.getId(), treeNode.getDataType()), treeNode);
        }

        for (T node : nodeMap.values()) {
            T parent = nodeMap.get(getTreeIdWithDataType(node.getPid(), "DIRECTORY"));
            if (parent != null) {
                parent.getChildren().add(node);
                continue;
            }

            result.add(node);
        }

        return result;
    }

    private static String getTreeIdWithDataType(Long id, String dataType) {
        return id + ":" + dataType;
    }

    /**
     * 根据pid，构建树节点
     */
    public static <T extends TreeNode> List<T> buildByLevel(List<T> treeNodes, Long pid) {
        //pid不能为空
        AssertUtils.isNull(pid, "pid");

        List<T> treeList = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (pid.equals(treeNode.getPid())) {
                treeList.add(findChildrenByLevel(treeNodes, treeNode));
            }
        }

        return treeList;
    }

    /**
     * 查找子节点
     */
    private static <T extends TreeNode> T findChildrenByLevel(List<T> treeNodes, T rootNode) {
        for (T treeNode : treeNodes) {
            if (rootNode.getId().equals(treeNode.getPid()) && rootNode.getLevel() == treeNode.getLevel() - 1) {
                rootNode.getChildren().add(findChildrenByLevel(treeNodes, treeNode));
            }
        }
        return rootNode;
    }

}