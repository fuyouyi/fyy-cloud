package com.fyy.mybatis.config;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fyy.mybatis.interceptor.MyDataPermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * mybatis-plus 数据权限配置
 *
 * @author fuyouyi
 * @since  2021-01-26
 */
@Configuration
public class MybatisPlusConfig {


    @Resource(name = "myTenantHandler")
    private MyTenantHandler tenantHandler;

    @Resource(name = "myDataPermissionHandler")
    private DataPermissionHandler dataPermissionHandler;

    /**
     * 配置数据权限
     */
    @Bean
    @Order(1)
    public MybatisPlusInterceptor dataFilterInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        List<InnerInterceptor> sqlParserList = new ArrayList<>();

//        // 租户隔离
//        if (!(StrUtil.isBlank(tenantHandler.getTenantIdColumn()) || CollUtil.isEmpty(tenantHandler.getTables()))) {
//            TenantLineInnerInterceptor tenantSqlParser = new TenantLineInnerInterceptor();
//            tenantSqlParser.setTenantLineHandler(tenantHandler);
//            sqlParserList.add(tenantSqlParser);
//        }

        // 数据权限
        MyDataPermissionInterceptor dataPermissionInterceptor = new MyDataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler(dataPermissionHandler);
        sqlParserList.add(dataPermissionInterceptor);

        if(CollUtil.isNotEmpty(sqlParserList)){
            mybatisPlusInterceptor.setInterceptors(sqlParserList);
        }

        return mybatisPlusInterceptor;
    }


    /**
     * 配置分页
     */
    @Bean
    @Order(0)
    public PaginationInnerInterceptor paginationInterceptor() {
        return new PaginationInnerInterceptor();
    }

}