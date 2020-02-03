package com.heim.wowauctions.common.persistence.solr;

import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class DateToTsConverter implements Converter<Date, Timestamp> {

    //INSTANCE;

    @Override
    public Timestamp convert(Date date) {
        return new Timestamp(date.getTime());
    }
}
