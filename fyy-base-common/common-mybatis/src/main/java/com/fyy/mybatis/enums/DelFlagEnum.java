package com.fyy.mybatis.enums;

import lombok.AllArgsConstructor;

/**
 * 删除标识枚举类
 *
 * @author carl
 * @since 1.0.0
 */
@AllArgsConstructor
public enum DelFlagEnum {
    NORMAL(0),
    DEL(1);

    private int value;

    public int value() {
        return this.value;
    }
}
