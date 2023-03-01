package com.fyy.common.tools.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * db数据和三方平台数据进行比对同步工具类
 * 1. 需要实现对应的逻辑
 * 2. 不适用于数据量过大的场景
 *
 * @author fuyouyi
 * @since 2021/04/26
 */
@NoArgsConstructor
public abstract class ListComparator<T> {

    private List<T> localList;

    private List<T> remoteList;

    @Getter
    protected List<T> addList = new ArrayList<>();

    @Getter
    protected List<T> deleteList = new ArrayList<>();

    @Getter
    protected List<T> updateList = new ArrayList<>();

    protected ListComparator(List<T> localList, List<T> remoteList) {
        this.localList = localList;
        this.remoteList = remoteList;
    }

    public ListComparator<T> process() {
        Map<String, T> localMap = localList.stream().collect(Collectors.toMap(this::compareKey, e -> e, (e1, e2) -> e2));
        Map<String, T> remoteMap = remoteList.stream().collect(Collectors.toMap(this::compareKey, e -> e, (e1, e2) -> e2));

        localMap.forEach((key, localData) -> {
            if (isIgnore(localData)) {
                return;
            }
            // 本地远程都有, 对比字段后更新
            if (remoteMap.containsKey(key)) {
                T remoteData = remoteMap.get(key);
                T updateData = updateData(localData, remoteData);
                if (updateData != null) {
                    updateList.add(updateData);
                }
            }
            // 远程没有, 本地有, 需要标记为历史数据
            else {
                if (!isDelete(localData)) {
                    deleteList.add(localData);
                }
            }
        });
        remoteMap.forEach((key, remoteData) -> {
            if (isIgnore(remoteData)) {
                return;
            }
            // 远程有, 本地没有，需要新增
            if (!localMap.containsKey(key)) {
                addList.add(remoteData);
            }
        });
        return this;
    }

    /**
     * key
     */
    protected abstract String compareKey(T data);

    /**
     * 忽略对比
     */
    protected abstract boolean isIgnore(T data);

    /**
     * TODO 建议在对比过程中，把 localData的id，设置到remoteData里面
     *
     * @return T 返回对象为空时, 代表两个对象一致
     */
    protected abstract T updateData(T localData, T remoteData);

    protected abstract boolean isDelete(T data);
}
