package com.fyy.mybatis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 租户权限字段枚举
 *
 * @author fuyouyi
 * @since  2021/04/20
 */
@Getter
@AllArgsConstructor
public enum TenantColumnEnum {

    merchant_id("merchant_id", "店铺id");

    private final String column;
    private final String desc;
}
