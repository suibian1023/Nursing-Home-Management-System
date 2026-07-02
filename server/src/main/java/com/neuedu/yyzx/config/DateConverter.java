package com.neuedu.yyzx.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateConverter implements Converter<String, Date> {
    private static final String[] FORMATS = {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyyMMdd"
    };

    @Override
    public Date convert(String source) {
        if (source == null || source.trim().isEmpty()) return null;
        source = source.trim();
        for (String fmt : FORMATS) {
            try {
                return new SimpleDateFormat(fmt).parse(source);
            } catch (ParseException ignored) {}
        }
        return null;
    }
}
