package com.fyy.common.tools.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fyy.common.tools.exception.RenException;

import java.lang.reflect.Field;
import java.util.*;

public class SyncLimitReadListener extends AnalysisEventListener<Object> {

    private List<Object> list = new ArrayList<Object>();

    Class clazz;

    public SyncLimitReadListener() {
        super();
    }

    public SyncLimitReadListener(Class clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public void invoke(Object object, AnalysisContext context) {
        if (list.size() < 10001) {
            list.add(object);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (list.size() > 10000) {
            throw new RenException("文件不允许超过10000行");
        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if(headMap.get(0).contains("填写须知")){
            return;
        }
        Map<Integer, String> head;
        if(clazz == null){
            return;
        }
        try {
            head = getIndexNameMap(clazz);   //通过class获取到使用@ExcelProperty注解配置的字段
        } catch (NoSuchFieldException e) {
           throw new RenException("文件内容错误");
        }
        Set<Integer> keySet = head.keySet();  //解析到的excel表头和实体配置的进行比对
        for (Integer key : keySet) {
            if (StrUtil.isEmpty(headMap.get(key))) {
                throw new RenException("表头第"+(key+1)+"列为空，请参照模板填写");
            }
            if (!headMap.get(key).equals(head.get(key))) {
                throw new RenException("表头第"+(key+1)+"列【"+headMap.get(key)+"】与模板【"+head.get(key)+"】不一致，请参照模板填写");
            }
        }

    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public Map<Integer, String> getIndexNameMap(Class clazz) throws NoSuchFieldException {
        Map<Integer, String> result = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        //获取类中所有的属性
        for (Field field : fields) {
            field.setAccessible(true);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);//获取根据注解的方式获取ExcelProperty修饰的字段
            if (excelProperty != null) {
                int index = excelProperty.index();         //索引值
                StringBuilder value = new StringBuilder();
                value.append(excelProperty.value()[1]);
                result.put(index, value.toString());
            }
        }
        return result;
    }
}