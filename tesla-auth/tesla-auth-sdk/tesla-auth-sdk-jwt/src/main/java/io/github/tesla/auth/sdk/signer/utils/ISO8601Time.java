package io.github.tesla.auth.sdk.signer.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ISO8601Time {
    static String TIME_ZONE = "Asia/Shanghai";
    static String DATA_FORMAT = "yyyyMMdd'T'HHmmss'Z'";

    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone(TIME_ZONE);
        DateFormat df = new SimpleDateFormat(DATA_FORMAT);
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    public static Date getDateFromIsoDateString(String dateAsISO) throws Exception {
        TimeZone tz = TimeZone.getTimeZone(TIME_ZONE);
        DateFormat df = new SimpleDateFormat(DATA_FORMAT);
        df.setTimeZone(tz);
        return df.parse(dateAsISO);
    }
}