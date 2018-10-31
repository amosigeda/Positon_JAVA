package com.platform.constant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
    public static Locale LOCALE = Locale.getDefault();
    public static String LOCALE_STR = LOCALE.toString();

    public static final String DEFAULT_BLANK = "";
    public static final String DEFAULT_SPACE = " ";
    public static final Object DEFAULT_OBJECT = new Object();

    public static final SimpleDateFormat FMT_DATE_TIME = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "yyyy-MM-dd HH:mm:ss" : "M/d/yyyy HH:mm:ss", LOCALE);
    public static final SimpleDateFormat FMT_DATE = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "yyyy-MM-dd" : "M/d/yyyy", LOCALE);
    public static final SimpleDateFormat FMT_DATE_MONTH = new SimpleDateFormat(LOCALE_STR.equals("zh_CN") ? "MM-dd" : "MM/d", LOCALE);
    public static final SimpleDateFormat FMT_WEEK_MONTH_DATE_TIME = LOCALE_STR.equals("zh_CN") ? new SimpleDateFormat( "yyyy-MM-dd HH:mm EEEE" , Locale.CHINA):new SimpleDateFormat( "EEEE dd MMMMM - HH:mm", Locale.ENGLISH);
    public static final SimpleDateFormat FMT_WEEK_MONTH_DATE = LOCALE_STR.equals("zh_CN") ? new SimpleDateFormat( "yyyy-MM-dd EEEE" , Locale.CHINA):new SimpleDateFormat( "EEEE dd MMMMM", Locale.ENGLISH);
    public static final SimpleDateFormat FMT_TIME = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat FMT_TIME_MINUTE = new SimpleDateFormat("HH:mm", Locale.getDefault());

    /**
     * 保留两位小数，如果有的话
     */
    public static final DecimalFormat FMT_DECIMAL = new DecimalFormat("0.##");
    public static final DecimalFormat FMT_DECIMAL_LITE=new DecimalFormat("0.#");
    public static final DecimalFormat FMT_DECIMAL_LOC=new DecimalFormat("0.000000");
    /**
     * 强制保留两位小数
     */
    public static final DecimalFormat FMT_DECIMAL_FORCE = new DecimalFormat("0.00");

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
}
