package com.fyy.security.user;

import com.fyy.security.bo.ResourceBO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 *
 * @author carl
 * @since 1.0.0
 */
@Data
@Slf4j
public class UserDetail {
    private static final long serialVersionUID = 1L;
    /** 用户ID（user_id）*/
    private Long id;
    private Long merchantId;
    /** 账号 */
    private String username;
    /** 真实名称 */
    private String realName;

    private String headUrl;
    private Integer gender;
    private String email;

    private Integer status;

    /**
     * 用户类型：USER-普通用户，ADMIN-管理员
     */
    private String userType;
    /**
     * 是否被管理员踢出   0：正常   1：被踢出，无权调用接口
     */
    private int kill;
    /**
     * 用户资源列表
     */
    private List<ResourceBO> resourceList;
    /**
     * 用户权限数据
     * key：权限code  PermissionCodeEnum.code
     * value:权限数据
     */
    private Map<String, Permission> permission;
}