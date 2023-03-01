package com.fyy.common.tools.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 序列号/业务编号生成器
 *
 * @author lvbin
 * @since 2020/1/19
 */
public class SequenceHelper {

    /**
     * 生成账户编码:"16" + 时间戳（保证不重复） + 买家id按100取模（方便以后按用户水平拆分）
     */
    public static String generateAccountNo(Long memberId) {
        StringBuilder sb = new StringBuilder("16");
        sb.append(System.currentTimeMillis());
        sb.append(MallStringUtils.leftPadding(memberId % 100, 0, 2));
        return sb.toString();
    }

    private static Long currentTimeInMillions(Date paramDate) {
        return System.currentTimeMillis() - DateUtils.parse(DateUtils.format(paramDate, DateUtils.DATE_PATTERN), DateUtils.DATE_PATTERN).getTime();
    }

    /**
     * 生成18位订单编号:8位日期 + 时间因子（保证不重复） + 随机数 + 买家id按100取模（方便以后按用户水平拆分）
     */
    public static String generateOrderSn(Long memberId) {
        return generateOrderSn(memberId, new Date());
    }

    /**
     * 生成订单号。重载方法，支持传入时间
     *
     * @param businessId
     * @param paramDate
     * @return
     */
    public static String generateOrderSn(Long businessId, Date paramDate) {
        StringBuilder sb = new StringBuilder();
        if (null == paramDate) {
            paramDate = new Date();
        }
        if (null == businessId) {
            businessId = 0L;
        }
        String date = new SimpleDateFormat("yyyyMMdd").format(paramDate);
        sb.append(date);
        sb.append(currentTimeInMillions(paramDate));
        sb.append(MallStringUtils.leftPadding(new Random().nextInt(100), 0, 2));
        sb.append(MallStringUtils.leftPadding(businessId % 100, 0, 2));
        return sb.toString();
    }

    /**
     * 提现流水号生成器
     *
     * @return
     */
    public static String generateWithdrawNo() {
        StringBuilder sb = new StringBuilder("WD");
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        sb.append(date);
        sb.append(System.currentTimeMillis() / 100);
        return sb.toString();
    }

    /**
     * 充值流水号生成器
     *
     * @return
     */
    public static String generateRechargeNo() {
        StringBuilder sb = new StringBuilder("RC");
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        sb.append(date);
        sb.append(System.currentTimeMillis() / 100);
        return sb.toString();
    }

    /**
     * 生成支付系统流水号：时间戳+随机数，用于传给银行的时候保证不重复
     *
     * @return
     */
    public static String generatePaySysSerialNo() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(MallStringUtils.leftPadding(new Random().nextInt(100), 0, 2));
        return sb.toString();
    }
}
