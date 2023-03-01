package com.fyy.common.tools.annotation;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class EnumsResultDTO {

    private String enumName;

    private JSONObject entryMap;
}
