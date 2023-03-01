package com.fyy.common.tools.constant;

/**
 * MQ常量类
 *
 * @author lvbin
 * @since 2020/1/29
 */
public interface MQConstant {

    /**
     * 单一用途的topic，默认tag
     */
    String DEFAULT_TAG = "DEFAULT";


    /**
     * 订单事件topic
     */
    String TOPIC_ORDER = "ORDER_EVENT";

    /**
     * 订单事件 - 创建
     */
    String TAG_ORDER_CREATE = "CREATE";

    /**
     * 订单事件 - 取消
     */
    String TAG_ORDER_CANCEL = "CANCEL";

    /**
     * 订单事件 - 支付成功
     */
    String TAG_ORDER_PAY = "PAY";

    /**
     * 订单事件 - 整单发货
     */
    String TAG_ORDER_SHIPPING = "SHIPPING";

    /**
     * 订单事件 - 部分发货
     */
    String TAG_ORDER_PART_SHIPPED = "PART_SHIPPED";

    /**
     * 订单事件 - 收货
     */
    String TAG_ORDER_RECEIVED = "RECEIVED";

    /**
     * 订单事件 - 评价
     */
    String TAG_ORDER_COMMENT = "COMMENT";

    /**
     * 订单事件 - 改价格
     */
    String TAG_ORDER_CHANGE_PRICE = "CHANGE_PRICE";

    /**
     * 订单事件 - 修改地址
     */
    String TAG_ORDER_CHANGE_ADDRESS = "CHANGE_ADDRESS";

    /**
     * 订单事件 - 修改物流
     */
    String TAG_ORDER_CHANGE_LOGISTICS = "CHANGE_LOGISTICS";

    /**
     * 订单事件 - 增加商家备注
     */
    String TAG_ORDER_ADD_MERCHANT_NOTE = "ADD_MERCHANT_NOTE";

    /**
     * 订单事件 - 增加商家备注
     */
    String TAG_ORDER_ADD_MEMBER_NOTE = "ADD_MEMBER_NOTE";

    /**
     * 订单事件 - 退款申请
     */
    String TAG_ORDER_REFUND_APPLY = "REFUND_APPLY";

    /**
     * 订单事件 - 退款关闭
     */
    String TAG_ORDER_REFUND_CLOSE = "REFUND_CLOSE";

    /**
     * 订单事件 - 退款成功
     */
    String TAG_ORDER_REFUND_SUCCESS = "REFUND_SUCCESS";


    /**
     * 发送邮件、短信 topic
     */
    String TOPIC_MESSAGE = "MESSAGE_EVENT";
    /**
     * 发送邮件Tag
     */
    String TAG_MESSAGE_EMAIL = "SEND_EMAIL";

    /**
     * 广告计划批量任务事件topic
     */
    String TOPIC_AD_TASK = "AD_TASK_EVENT";

    /**
     * 千川广告计划批量任务事件topic
     */
    String TOPIC_QC_TASK = "QC_TASK_EVENT";

    /**
     * 抖音订单事件topic
     */
    String TOPIC_DOUYIN_ALLIANCE_ORDER = "DOUYIN_ALLIANCE_ORDER_EVENT";

    /**
     * 广告实体计划任务事件topic
     */
    String TOPIC_AD_ENTITY_REPORT = "AD_ENTITY_REPORT_EVENT";

    /**
     * 广告计划批量任务事件 - 启动
     */
    String TAG_AD_TASK_START = "START";

    /**
     * 千川广告计划批量任务事件 - 启动
     */
    String TAG_QC_TASK_START = "START";

    /**
     * 抖音订单事件 - 启动
     */
    String TAG_DOUYIN_ALLIANCE_ORDER_START = "START";

    /**
     * 广告实体计划任务事件 - 完成
     */
    String TOPIC_AD_ENTITY_REPORT_FINISH = "FINISH";

    /**
     * [kuaishou] 金牛广告计划批量任务TOPIC
     */
    String TOPIC_KS_TASK = "KS_TASK_EVENT";


    /**
     * [thirdshop] 抖店订单事件topic
     */
    String TOPIC_DOUDIAN_TOPIC_ORDER = "DOUDIAN_ORDER_EVENT";

    /**
     * [wms] 发货任务topic
     */
    String TOPIC_WMS_SHIPPING_TASK = "WMS_SHIPPING_TASK";

    /**
     * [pms] 商品事件topic
     */
    String TOPIC_PMS_PRODUCT = "PMS_PRODUCT_EVENT";

    /**
     * 商品自定义编码-变更
     */
    String TAG_PRODUCT_CUSTOM_CODE_CHANGE = "PRODUCT_CUSTOM_CODE_CHANGE";

    /**
     * SKU自定义编码-变更
     */
    String TAG_SKU_CUSTOM_CODE_CHANGE = "SKU_CUSTOM_CODE_CHANGE";


    /**
     * 商家消息主题
     */
    String TOPIC_MMS_SHOP = "MMS_SHOP_EVENT";
    /**
     * TAG 店铺换绑
     */
    String TAG_MMS_SHOP_CHANGE_BIND = "CHANGE_BIND";

    /**
     * TOPIC 授权回调消息
     */
    String TOPIC_AUTH_EVENT = "AUTH_EVENT";
    /**
     * TAG Qianchuan授权回调业务系统
     */
    String TAG_QIANCHUAN_AUTH_INIT = "QIANCHUAN_INIT";
    /**
     * TAG Qianchuan授权回调mms系统
     */
    String TAG_MMS_INVITER_REWARD = "MMS_INVITER_REWARD";


    /**
     * TOPIC 报表拉取消息
     */
    String TOPIC_REPORT_EVENT = "REPORT_EVENT";
    /**
     * TAG Qianchuan广告账号日报拉取
     */
    String TAG_QIANCHUAN_ADVERTISER_DAILY_PULL = "QIANCHUAN_ADVERTISER_DAILY_PULL";


    /**
     * 千川更新任务事件topic
     */
    String TOPIC_QC_UPDATE_TASK = "QC_UPDATE_TASK_EVENT";

    /**
     * 千川更新任务事件 - 启动
     */
    String TAG_QC_UPDATE_TASK_START = "START";

}
