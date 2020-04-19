package com.mmj.order.utils.pay;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mmj.common.utils.DateUtils;

import java.io.IOException;
import java.util.Date;


public class CustomDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {
        String dateString = DateUtils.getDate(date, DateUtils.DATE_PATTERN_1);
        jsonGenerator.writeString(dateString);
    }

}
