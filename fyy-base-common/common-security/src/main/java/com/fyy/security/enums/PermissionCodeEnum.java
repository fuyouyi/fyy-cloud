package com.fyy.security.enums;

import com.fyy.common.tools.annotation.EnumsAutoScan;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EnumsAutoScan
public enum PermissionCodeEnum {
    CUSTOM_MANAGE("2", "客户中心-客户信息管理"),
    PROVIDER_MANAGE("3", "客户中心-供应商合作管理"),
    RELATION_MANAGE("4", "客户中心-跟进关系管理"),
    COLLECTION_MANAGE("5", "客户中心-客户信息采集"),
    COMPANNY_SET("7", "系统设置-公司设置"),
    ROLE_PERMISSION("8", "系统设置-角色权限"),
    APPROVAL("9", "系统设置-审批中心"),
    VISIT("10", "辅导/协访"),
    INSPECTION_PALN("26", "门店巡查-巡查计划"),
    SIGN("12", "签到管理"),
    SALE_ASSESSMENT("13", "销售考核"),
    MEET("14", "会议/活动"),
    SALE_DATA("15", "销售数据"),
    ACHIEVEMENTS_WORK("28", "绩效看板-工作汇报"),
    ACHIEVEMENTS_COMPLETE("29", "绩效看板-销量达成"),
    ACHIEVEMENTS_PROCESS("30", "绩效看板-销量进度"),
    REPORT_SALEOVERVIEW("19", "报表-销售概览"),
    REPORT_SALEAMOUNT("20", "报表-销售金额看板"),
    REPORT_SALESIZE("21", "报表-动销品规看板"),
    REPORT_SALECUSTOM("22", "报表-动销客户看板"),
    REPORT_ORDER("23", "报表-订单分析看板"),
    REPORT_ACTION("24", "报表-行为分析看板"),
    REPORT_ORDERSEARCH("25", "报表-订单查询（APP）"),
    ;

    /**
     * code
     */
    private final String menuId;


    /**
     * msg
     */
    private final String msg;

    public static PermissionCodeEnum getEnumByCode(String menuId){
        for(PermissionCodeEnum permissionCodeEnum : PermissionCodeEnum.values()){
            if(permissionCodeEnum.menuId.equals(menuId)){
                return permissionCodeEnum;
            }
        }
        return null;
    }
}
