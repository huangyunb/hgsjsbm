package com.jsbm.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Project: com.school.utils
 * @Author: Silly-episode(DYZ89)
 * @Date: 2022/4/21 13:31
 * @FileName: com.school.utils
 * @Description:
 */
public class Time {

    public static String nowDate(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static String nowDateTime(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }

    public static String getStamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static Date getNow() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String Time=df.format(new Date());// new Date()为获取当前系统时间

        Date date = df.parse(Time);
        return date;
    }
}
