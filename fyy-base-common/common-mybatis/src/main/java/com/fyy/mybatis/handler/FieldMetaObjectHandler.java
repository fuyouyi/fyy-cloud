package com.fyy.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.fyy.security.user.SecurityUser;
import com.fyy.mybatis.enums.DelFlagEnum;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 公共字段，自动填充值
 *
 * @author carl
 * @since 1.0.0
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {
    private final static String CREATE_DATE = "createTime";
    private final static String CREATOR = "creator";
    private final static String UPDATE_DATE = "updateTime";
    private final static String UPDATOR = "updator";
    private final static String DEL_FLAG = "delFlag";
    private final static String DEPT_ID = "deptId";

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = SecurityUser.getUserId();
        if(userId == null) userId = 0L;

        //创建者
        setFieldValByName(CREATOR, userId, metaObject);
        //更新者
        setFieldValByName(UPDATOR, userId, metaObject);
        //创建者所属部门
        // setFieldValByName(DEPT_ID, user.getDeptId(), metaObject);
        Date date = new Date();

        //创建时间
        if (metaObject.getValue(CREATE_DATE) == null)
        setFieldValByName(CREATE_DATE, date, metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, date, metaObject);

        //删除标识
        setFieldValByName(DEL_FLAG, DelFlagEnum.NORMAL.value(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新者
        setFieldValByName(UPDATOR, SecurityUser.getUserId()==null?0:SecurityUser.getUserId(), metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, new Date(), metaObject);
    }
}