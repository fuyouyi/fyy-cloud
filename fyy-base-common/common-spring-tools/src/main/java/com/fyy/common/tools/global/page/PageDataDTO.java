package com.fyy.common.tools.global.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "分页数据入参")
public class PageDataDTO {

    @ApiModelProperty(value = "当前页码，从0开始", required = true, example = "0")
    private String page = "0";
    @ApiModelProperty(value = "每页显示记录数", required = true, example = "10")
    private String limit = "10";
    @ApiModelProperty(value = "排序字段")
    private String orderField;
    @ApiModelProperty(value = "排序方式，可选值(asc、desc）")
    private String order = "desc";
}
