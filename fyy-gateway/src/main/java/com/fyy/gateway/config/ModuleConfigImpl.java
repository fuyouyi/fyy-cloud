package com.fyy.gateway.config;

import com.fyy.common.tools.config.ModuleConfig;
import org.springframework.stereotype.Service;

/**
 * 模块配置信息
 *
 * @author carl
 * @since 1.0.0
 */
@Service
public class ModuleConfigImpl implements ModuleConfig {
    @Override
    public String getName() {
        return "gateway";
    }
}