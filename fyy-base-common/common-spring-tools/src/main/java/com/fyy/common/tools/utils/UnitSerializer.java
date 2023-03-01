package com.fyy.common.tools.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author bill
 * @description
 * @date 2022/12/20
 */
public class UnitSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(value == null || BigDecimal.ZERO.equals(value)){
            jsonGenerator.writeNumber(value);
        }else {
            BigDecimal newValue = value.divide(BigDecimal.valueOf(10000), 2, BigDecimal.ROUND_HALF_UP);
            jsonGenerator.writeNumber(newValue);
        }
    }
}
