package com.fyy.security.enums;

/**
 * 用户被踢出枚举
 *
 * @author carl
 * @since 1.0.0
 */
public enum UserKillEnum {
    YES(1),
    NO(0);

    private int value;

    UserKillEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
