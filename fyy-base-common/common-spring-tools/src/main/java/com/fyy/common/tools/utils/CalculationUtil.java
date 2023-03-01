package com.fyy.common.tools.utils;

/**
 * 计算工具类
 */
public class CalculationUtil {

    /**
     * 数组最大值
     */
    public static int max(int[] sz){
        int max = Integer.MIN_VALUE;
        for(int s : sz){
            max = Math.max(max, s);
        }
        return max;
    }
}
