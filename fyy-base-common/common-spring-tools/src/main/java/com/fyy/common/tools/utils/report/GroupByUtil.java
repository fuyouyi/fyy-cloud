package com.fyy.common.tools.utils.report;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyy
 */
public class GroupByUtil {

    public static <T extends GroupByDTO> void fillZero(Map<String, T> groupByMap, List<String> fillKey, T deFaultGroupByDTO){
        fillKey.forEach( key -> {
            if ( !groupByMap.containsKey( key )){
                groupByMap.put(key, (T) deFaultGroupByDTO.newDeFaultGroupByDTO(key));
            }
        } );
    }


    public static void main(String[] args) {
        // sql查出来的结果
        Map<String, Haha> groupByMap = new HashMap<>();
        groupByMap.put("A", new Haha("A", 85, 96));
        groupByMap.put("B", new Haha("B", 31, 25));

        // 需要的groupBy集合
        List<String> fillKey = CollUtil.toList("A", "B", "C", "D");

        fillZero(groupByMap, fillKey, new Haha());

        System.out.println(JSONObject.toJSONString(groupByMap));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Haha implements GroupByDTO{
        String key;

        Integer value;

        Integer value2;


        // 默认填充参数
        @Override
        public GroupByDTO newDeFaultGroupByDTO(String key) {
            return new Haha(key, 1, 1);
        }
    }
}
