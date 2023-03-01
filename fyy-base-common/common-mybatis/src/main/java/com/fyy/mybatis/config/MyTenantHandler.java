package com.fyy.mybatis.config;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.fyy.security.user.SecurityUser;
import com.fyy.security.user.UserDetail;
import lombok.Data;
import com.fyy.mybatis.enums.TenantColumnEnum;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.ValueListExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 租户表配置
 *
 * @author fuyouyi
 * @since  2021-01-26
 */
@Data
@Configuration("myTenantHandler")
public class MyTenantHandler implements TenantLineHandler {

    @Value("${mybatis-plus.tenant.tenantIdColumn:}")
    private String tenantIdColumn;

    @Value("${mybatis-plus.tenant.tables:}")
    private Set<String> tables;

    @Override
    public Expression getTenantId() {
        ValueListExpression listExpression = new ValueListExpression();
        if(!systemOp()  && tenantIdColumn!=null){
            if(TenantColumnEnum.merchant_id.getColumn().equals(tenantIdColumn)){
                listExpression.setExpressionList(
                        new ExpressionList(CollUtil.toList(new LongValue(1L)))
                );
            }
        }
        return listExpression;
    }

    @Override
    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return !tables.contains(tableName);
    }

    private boolean systemOp(){
        UserDetail userDetail = SecurityUser.getUser();
        return userDetail == null;
    }
}
