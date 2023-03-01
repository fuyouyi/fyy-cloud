package com.fyy.security.redis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.fyy.security.enums.UserKillEnum;
import com.fyy.security.user.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户Redis
 *
 * @author carl
 * @since 1.0.0
 */
@Component
public class UserDetailRedis {
    @Autowired
    private RedisUtils redisUtils;

    public void set(UserDetail user, long expire) {
        if (user == null) {
            return;
        }
        String key = RedisKeys.getSecurityUserKey(user.getId());
        //bean to map
        user.setKill(UserKillEnum.NO.value());
        Map<String, Object> map = BeanUtil.beanToMap(user, false, true);
        redisUtils.hMSet(key, map, expire);

        //用户登录时，清空菜单导航、权限标识
        refreshMenu(user.getId());
    }

    public UserDetail get(Long id) {
        String key = RedisKeys.getSecurityUserKey(id);

        Map<String, Object> map = redisUtils.hGetAll(key);
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        //map to bean
        return BeanUtil.mapToBean(map, UserDetail.class, true);
    }

    public void refresh(Long userId) {
        // 刷新用户缓存
        String userIdKey = RedisKeys.getSecurityUserKey(userId);
        redisUtils.delete(userIdKey);
        refreshMenu(userId);
    }

    /**
     * 用户退出
     *
     * @param id 用户ID
     */
    public void logout(Long id) {
        if(id == null){
            return;
        }
        String key = RedisKeys.getSecurityUserKey(id);
        redisUtils.hSet(key, "kill", UserKillEnum.YES.value());

        //清空菜单导航、权限标识
        refreshMenu(id);
    }


    public void refreshMenu(Long userId) {
        //清空菜单导航、权限标识
        redisUtils.deleteByPattern(RedisKeys.getUserMenuNavKey(userId));
        redisUtils.delete(RedisKeys.getUserPermissionsKey(userId));
    }

    public Collection<String> loggingKeyList() {
        return redisUtils.keys("sys:security:user:*");
    }

    public Long getExpire(Long userId) {
        String key = RedisKeys.getSecurityUserKey(userId);
        return redisUtils.getExpire(key, TimeUnit.SECONDS);
    }
}