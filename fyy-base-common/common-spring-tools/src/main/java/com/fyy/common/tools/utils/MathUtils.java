package com.fyy.common.tools.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author lvbin
 * @since 2020/3/22
 */
public class MathUtils {

    private static final double ZERO = 0.000001;

    /**
     * 判断两个浮点数是否相等
     * 由于计算机浮点数计算存在精度误差，因此比较两个经过运算后的浮点数是否相等，不能用 == 或 equals，
     * 必须将两个数相减，看其结果的绝对值是否非常接近于0
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean equals(Double d1, Double d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("入参不能为null");
        }
        return Math.abs(d1 - d2) < ZERO;
    }

    /**
     * 判断两个浮点数是否相等
     * 由于计算机浮点数计算存在精度误差，因此比较两个经过运算后的浮点数是否相等，不能用 == 或 equals，
     * 必须将两个数相减，看其结果的绝对值是否非常接近于0
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean equals(BigDecimal d1, BigDecimal d2) {
        if (d1 == null || d2 == null) {
            throw new IllegalArgumentException("入参不能为null");
        }
        return equals(d1.doubleValue(), d2.doubleValue());
    }

    /**
     * 四舍五入，保留两位小数
     *
     * @param d
     * @return
     */
    public static BigDecimal halfUp(BigDecimal d) {
        Assert.notNull(d, "入参不能为空");
        return d.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入，保留n小数
     *
     * @param d
     * @param n
     * @return
     */
    public static BigDecimal halfUp(BigDecimal d, int n) {
        Assert.notNull(d, "入参不能为空");
        return d.setScale(n, RoundingMode.HALF_UP);
    }

    /**
     * 转化为百分比
     *
     * @param d
     * @return
     */
    public static String convertPercent(BigDecimal d) {
        Assert.notNull(d, "入参不能为空");
        return StrUtil.format("{}%", halfUp(d.multiply(BigDecimal.valueOf(100)), 2));
    }

    /**
     * 将百分比转化为小数
     *
     * @param d
     * @return
     */
    public static BigDecimal convertBigDecimal(String d) {
        Assert.hasText(d, "入参不能为空");
        BigDecimal l;
        try {
            Float sub = Float.parseFloat(d.substring(0, d.indexOf("%")));
            l = BigDecimal.valueOf(sub);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("非数字类型字符串");
        }
        return l.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

    /**
     * 常用的保存位数
     */
    public static final String FOUR = "0.0000";//保留4位小数
    public static final String TWO = "0.00";

    /**
     * 整数相除保留小数
     *
     * @param a
     * @param b
     * @return
     */
    public static Double halfUpInteger(Integer a, Integer b) {
        double num = (double) a / b;
        DecimalFormat df = new DecimalFormat(FOUR);
        return Double.parseDouble(df.format(num));
    }


    public static BigDecimal convertBigDecimalHalfUp(Integer i) {
        if (i == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(i).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param i
     * @param div 除数
     * @return
     */
    public static BigDecimal convertBigDecimalHalfUp(Integer i, Integer div) {
        if (i == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(i / div).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @param i
     * @param div 除数
     * @return
     */
    public static BigDecimal convertBigDecimalHalfUp(BigDecimal i, Integer div) {
        if (i == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(i.doubleValue() / div).setScale(2, RoundingMode.HALF_UP);
    }
}