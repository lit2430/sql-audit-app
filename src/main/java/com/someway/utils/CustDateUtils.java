package com.someway.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @Author lit
 * @Date 2024-07-06 11:21
 **/
public class CustDateUtils {


    public static final String YYYY_MM_DD_HH_mm_SS = "yyyy-MM-dd HH:mm:ss";



    public static String tsToDateStr(Long timestamp, String format) {
        if (timestamp == null) {
            return null;
        }
        return toDateStr(toLocalDateTime(timestamp), format);
    }


    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault());
    }


    public static String toDateStr(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);

    }


}
