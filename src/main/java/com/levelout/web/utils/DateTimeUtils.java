package com.levelout.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String getCurrentlyDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }
}
