package com.fyy.security.user;

import cn.hutool.core.util.StrUtil;
import com.fyy.common.tools.config.AutowireInitializingStaticUtil;
import com.fyy.common.tools.constant.Constant;
import com.fyy.common.tools.exception.RenException;
import com.fyy.common.tools.utils.HttpContextUtils;
import com.fyy.security.redis.UserDetailRedis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 用户
 *
 * @author carl
 * @since 1.0.0
 */
@Slf4j
@Component
public class SecurityUser extends AutowireInitializingStaticUtil {

    @Autowired
    private static UserDetailRedis userDetailRedis;

    /**
     * 测试
     */
    public static UserDetail testUser() {
        return userDetailRedis.get(1295619787862573057L);
    }

    public static void sysOpCheck() {
        Long userId = getUserId();
        if (userId != null) {
            log.error("You can't do that, userId = {}", userId);
            throw new RenException("You can't do that, userId = " + userId);
        }
    }

    /**
     * 获取用户信息
     */
    public static UserDetail getUser() {
        Long userId = getUserId();
        if (userId == null) {
            throw new RenException("用户未登录");
        }
        return userDetailRedis.get(userId);
    }

    /**
     * 获取用户信息, 用户不存在，抛异常
     */
    public static UserDetail findUser() {
        Long userId = getUserId();
        if (userId == null) {
            throw new RenException("用户未登录");
        }
        return userDetailRedis.get(userId);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request == null) {
            return null;
        }

        String userId = request.getHeader(Constant.USER_KEY);
        if (StrUtil.isBlank(userId)) {
            return null;
        }
        return Long.parseLong(userId);
    }

    /**
     * 获取appType
     */
    public static String getAppType() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request == null) {
            return null;
        }

        String appType = request.getHeader(Constant.APP_TYPE);
        if (StrUtil.isBlank(appType)) {
            return null;
        }
        return appType;
    }

    /**
     * 获取用户ID
     */
    public static String getToken() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (request == null) {
            return null;
        }

        return request.getHeader(Constant.TOKEN_HEADER);
    }

    /**
     * 获取当前登录人的权限数据
     * key：权限code  PermissionCodeEnum.code
     */
    public static Permission getPermission(String key) {
        UserDetail user = getUser();
        if (user == null) {
            throw new RenException("用户未登录");
        }
        Map<String, Permission> permissionMap = user.getPermission();
        if(permissionMap == null){
            return null;
        }
        return permissionMap.get(key);
    }

    /**
     * 刷新用户缓存
     */
    public static void refreshUser(Long userId) {
        CompletableFuture.runAsync(() -> {
            if (userId == null) {
                return;
            }
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                log.debug("refreshUser 保存Redis-事务前准备保存Redis, id:{}", userId);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        userDetailRedis.refresh(userId);
                        log.debug("refreshUser 保存Redis-事务后保存Redis, id:{}", userId);
                    }
                });
            } else {
                userDetailRedis.refresh(userId);
                log.debug("refreshUser 保存Redis-无事务保存Redis, id:{}", userId);
            }
        });
    }

}