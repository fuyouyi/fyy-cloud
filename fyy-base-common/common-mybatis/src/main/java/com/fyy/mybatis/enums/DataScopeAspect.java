package com.fyy.mybatis.enums;

public interface DataScopeAspect {

    String DATA_SCOPE_NO = "NO";

    // 自己的

    String DATA_SCOPE_ME = "ME";

    // 基于属性的

    String DATA_SCOPE_ABAC = "ABAC";


    // 所在部门, 管理部门, 公司下

    String DATA_SCOPE_DEPT = "DEPT";

    String DATA_SCOPE_MANAGE_DEPT = "MANAGE_DEPT";

    String DATA_SCOPE_COMPANY = "COMPANY";
}
