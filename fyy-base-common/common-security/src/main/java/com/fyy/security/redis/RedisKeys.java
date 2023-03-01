package com.fyy.security.redis;

/**
 * @author carl
 * @since 1.0.0
 */
public class RedisKeys {
    /**
     * 系统参数Key
     */
    public static String getSysParamsKey() {
        return "sys:params";
    }

    /**
     * 登录验证码Key
     */
    public static String getLoginCaptchaKey(String uuid) {
        return "sys:captcha:" + uuid;
    }

    /**
     * 短信验证码Key
     */
    public static String getAuthCodeKey(String mobile, String scene) {
        return String.format("sys:authcode:%s:%s", scene, mobile);
    }

    /**
     * 短信验证码错误次数Key
     */
    public static String getAuthCodeErrorCountKey(String mobile, String scene) {
        return RedisKeys.getAuthCodeKey(mobile, scene) + ":errorCount";
    }

    /**
     * 登录用户Key
     */
    public static String getSecurityUserKey(Long id) {
        return "sys:security:user:" + id;
    }

    /**
     * 国药登录用户Key
     */
    public static String getBladeAuthUserKey(String username,String cusCode) {
        return String.format("blade:auth:user:%s:%s", username, cusCode);
    }

    /**
     * 系统日志Key
     */
    public static String getSysLogKey() {
        return "sys:log";
    }

    /**
     * 系统资源Key
     */
    public static String getSysResourceKey() {
        return "sys:resource";
    }

    /**
     * 用户菜单导航Key
     */
    public static String getUserMenuNavKey(Long userId, String language) {
        return "sys:user:nav:" + userId + "_" + language;
    }

    /**
     * 用户菜单导航Key
     */
    public static String getUserMenuNavKey(Long userId) {
        return "sys:user:nav:" + userId + "_*";
    }

    /**
     * 用户权限标识Key
     */
    public static String getUserPermissionsKey(Long userId) {
        return "sys:user:permissions:" + userId;
    }


    public static final String SYS_DICT_TYPE_KEY = "sys:dict:type";

    public static final String DEVICE_MONITOR_KEY = "eds:monitor:device";

    public static final String MONITOR_CONFIG_KEY = "eds:monitor:config";

    public static final String MONITOR_TYPE_KEY = "eds:monitor:type";

    public static final String VARIABLE_EXIST_KEY = "eds:variable:exist";

    /**
     * ADMIN-省市区缓存(抖店)
     */
    public static final String SYS_REGION_DOUDIAN_LEVEL_KEY = "sys:regionDouDian:level";

    /**
     * 国药token缓存
     */
    public static final String GY_TOKEN = "gy:token";

    /**
     * 字典缓存
     */
    public static final String GY_DICT = "gy:dict";

    /**
     * 字典缓存
     */
    public static final String GY_AREA = "gy:area";

    /**
     * 国药token缓存
     */
    public static final String REPORT_OVERVIEW = "report:overview";

    /**
     * 国药token缓存
     */
    public static final String REPORT_SELL_CUSTOMER_TRENDS = "report:sellCustomerTrends";


    /**
     * 国药token缓存
     */
    public static final String REPORT_OVERVIEW_RANKS = "report:overviewRanks";

    /**
     * 国药token缓存
     */
    public static final String REPORT_TOTAL_CUSTOMER_COUNT = "report:totalCustomerCount";

    /**
     * 国药token缓存
     */
    public static final String REPORT_SETTLE_AMOUNT_TREND = "report:settleAmount:trend";

    /**
     * 国药token缓存
     */
    public static final String REPORT_PRODUCT_SPEC = "report:productSpec";

    /**
     * 国药token缓存
     */
    public static final String REPORT_PRODUCT_REPORT = "report:product:report";

    /**
     * 国药token缓存
     */
    public static final String REPORT_PRODUCT_DETAIL_REPORT = "report:product:detailReport";

    /**
     * 国药token缓存
     */
    public static final String REPORT_CUSTOMER_REPORT = "report:customer:report";

    /**
     * 国药token缓存
     */
    public static final String REPORT_CUSTOMER_BOARD = "report:customer:board";

    /**
     * 国药token缓存
     */
    public static final String REPORT_CUSTOMER_BOARD_LIST = "report:customer:board:list";

    /**
     * 国药token缓存
     */
    public static final String REPORT_AREA_SELL_CUSTOMER_DETAIL = "report:area:sellCustomer:detail";


    /**
     * 销售报表
     */
    public static final String REPORT_SALE_REPORT = "report:sale:report";


    /**
     * 销售报表总计
     */
    public static final String REPORT_SALE_REPORT_TOTAL = "report:sale:report:total";



    /**
     * 销售看板
     */
    public static final String REPORT_SALE_BOARD = "report:sale:board";




    /**
     * 订单报表
     */
    public static final String REPORT_ORDER_REPORT = "report:order:report";

    /**
     * 订单报表总计
     */
    public static final String REPORT_ORDER_REPORT_TOTAL = "report:order:report:total";



    /**
     * 订单看板
     */
    public static final String REPORT_ORDER_BOARD = "report:order:board";



    /**
     * 行为报表
     */
    public static final String REPORT_BUYER_REPORT = "report:buyer:report";


    /**
     * 行为报表总计
     */
    public static final String REPORT_BUYER_REPORT_TOTAL = "report:buyer:report:total";




    /**
     * 行为看板
     */
    public static final String REPORT_BUYER_BOARD = "report:buyer:board";


    /**
     * 销售数据-monthList
     */
    public static final String SALE_DATA_MONTH_LIST = "sale:data:month:list";

    /**
     * 销售数据-page
     */
    public static final String SALE_DATA_PAGE = "sale:data:page";

    /**
     * 销售数据-summary
     */
    public static final String SALE_DATA_SUMMARY = "sale:data:summary";

    /**
     * 销售数据-names
     */
    public static final String SALE_DATA_NAMES = "sale:data:names";

    /**
     * 部门ids
     */
    public static final String COMMON_DEPT_IDS = "common:dept:ids";

    /**
     * 部门ids
     */
    public static final String REPORT_DEPT_IDS = "report:dept:ids";

    /**
     * 销售考核-是否创建中
     */
    public static final String SALE_CREATING = "sale:creating";

    /**
     * 销售考核-是否撤销中
     */
    public static final String SALE_UNDOING = "sale:undoing";


}
