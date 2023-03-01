package com.fyy.mybatis.utils;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Id生成工具类。用的是mybatis-plus自带的生成工具类生成。
 * @author: hzy
 * @since: 2020/08/20
 **/
@Component
public class IdUtil {
    private static volatile DefaultIdentifierGenerator generator;

    @PostConstruct
    public void init() {
        if (null == generator) {
            synchronized(IdUtil.class) {
                if (null == generator) {
                    generator =  new DefaultIdentifierGenerator();
                }
            }
        }
    }

    /**
     * 生成id
     * @return
     */
    public static Long genId(){
        return IdUtil.generator.nextId(null);
    }

    public static Long genShortId(){
        return DateUtil.currentSeconds() * 1000 + generator.nextId(null) % 1000;
    }
}
