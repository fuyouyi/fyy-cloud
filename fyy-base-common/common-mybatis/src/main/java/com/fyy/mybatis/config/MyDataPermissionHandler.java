package com.fyy.mybatis.config;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.fyy.common.tools.utils.ObjectUtils;
import com.fyy.security.user.SecurityUser;
import com.fyy.security.user.UserDetail;
import lombok.Data;
import com.fyy.mybatis.enums.DataScopeAspect;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 数据权限配置
 *
 * @author fuyouyi
 * @since  2021-01-26
 */
@Data
@Configuration("myDataPermissionHandler")
public class MyDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        try {
            Class<?> clazz = Class.forName(mappedStatementId.substring(0, mappedStatementId.lastIndexOf(".")));
            String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(".") + 1);
            Method[] methods = clazz.getDeclaredMethods();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dataScopeFilter(SecurityUser.getUser(), where, DataScopeAspect.DATA_SCOPE_DEPT);
    }

    /**
     *
     */
    public static Expression dataScopeFilter(UserDetail user, Expression where, String dataScope) {
        Expression expression = null;

        if (DataScopeAspect.DATA_SCOPE_NO.equals(dataScope)) {
            return where;
        }
        if (DataScopeAspect.DATA_SCOPE_ME.equals(dataScope)) {
            // 第一个( creator = 1 )
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(buildColumn("em.creator"));
            equalsTo.setRightExpression(new LongValue(user.getId()));

            // 第二个( monitor_id in ( select id from eds_monitor_config where creator = ?) )
            InExpression inExpression = new InExpression();
            inExpression.setLeftExpression(buildColumn("em.monitor_id"));

            // 子查询: (select id from eds_monitor_config where creator = 当前登录用户的ID)
            SubSelect subSelect = new SubSelect();
            PlainSelect select = new PlainSelect();
            select.setSelectItems(Collections.singletonList(new SelectExpressionItem(new Column("id"))));
            select.setFromItem(new Table("eds_monitor_config"));
            EqualsTo equalsTo2 = new EqualsTo();
            equalsTo2.setLeftExpression(new Column("eds_monitor_config.creator"));
            equalsTo2.setRightExpression(new LongValue(user.getId()));
            subSelect.setSelectBody(select);
            inExpression.setRightExpression(subSelect);

            // 合并第一个和第二个，用or条件
            OrExpression orExpression = new OrExpression(equalsTo, inExpression);
            expression = ObjectUtils.isNotEmpty(expression) ? new OrExpression(expression, orExpression) : orExpression;
        }
        if (DataScopeAspect.DATA_SCOPE_DEPT.equals(dataScope)) {
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(buildColumn("merchant_id"));
            equalsTo.setRightExpression(new LongValue(user.getMerchantId()));
            expression = ObjectUtils.isNotEmpty(expression) ? new OrExpression(expression, equalsTo) : equalsTo;
        }
        if (DataScopeAspect.DATA_SCOPE_MANAGE_DEPT.equals(dataScope)) {
            // 最外层资源id
            InExpression inExpression = new InExpression();
            inExpression.setLeftExpression(buildColumn("id"));

            SubSelect subSelect = new SubSelect();
            PlainSelect select = new PlainSelect();
            select.setSelectItems(Collections.singletonList(new SelectExpressionItem(new Column("dept_id"))));
            select.setFromItem(new Table("sys_dept"));
            EqualsTo equalsTo = new EqualsTo();
            equalsTo.setLeftExpression(new Column("dept_id"));
            equalsTo.setRightExpression(new LongValue(1L));

            Function function = new Function();
            function.setName("find_in_set");
            function.setParameters(new ExpressionList(new LongValue(1L) , new Column("ancestors")));
            select.setWhere(new OrExpression(equalsTo, function));
            subSelect.setSelectBody(select);
            inExpression.setRightExpression(subSelect);

            expression = ObjectUtils.isNotEmpty(expression) ? new OrExpression(expression, inExpression) : inExpression;
        }
        if (DataScopeAspect.DATA_SCOPE_ABAC.equals(dataScope)) {

        }

        return ObjectUtils.isNotEmpty(where) ? new OrExpression(where, new Parenthesis(expression)) : expression;
    }

    /**
     * 构建Column
     *
     * @param columnName 字段名称
     * @return 带表别名字段
     */
    public static Column buildColumn(String columnName) {
//        if (StrUtil.isNotEmpty(tableAlias)) {
//            columnName = tableAlias + "." + columnName;
//        }
        return new Column(columnName);
    }
}
