package com.fyy.common.tools.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Component;

@Component
public class PropertyUtil extends PropertySourcesPlaceholderConfigurer {

    private static PropertyResolver propertyResolver;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                     ConfigurablePropertyResolver propertyResolver) throws BeansException {
        super.processProperties(beanFactoryToProcess, propertyResolver);
        PropertyUtil.propertyResolver = propertyResolver;
    }

    public static String getString(String key) {
        return propertyResolver.getProperty(key);
    }

    public static boolean isDev() {
        return "dev".equalsIgnoreCase(propertyResolver.getProperty("spring.profiles.active"));
    }

    public static boolean isTest() {
        return "test".equalsIgnoreCase(propertyResolver.getProperty("spring.profiles.active"));
    }
}