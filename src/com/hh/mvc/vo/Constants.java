package com.hh.mvc.vo;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/8/27.
 */
public interface Constants {
    /**
     * File separator from System properties
     */
    public static final String FILE_SEP = System.getProperty("file.separator");

    public static final SimpleDateFormat COMM_DATETIME_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat COMM_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 任意字符，防止彩虹表爆破，一旦产生并存储过由此产生的MD5值，则不要再修改此值，否则所有相关的值需要重新生成。
     */
    public static final String[] MD5_SALTS = new String[]{"QMIMSY", "DXILVUSM", "YJNQMSWZM", "WANGZQM", "EMVYWXN"};

    /**
     * 地球半径（单位：米），来自NASA网站：
     * http://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html
     */
    public static final Double EARTH_RADIUS = 6371000D;


    public static final int TYPE_SERVICE = 1;
    public static final int TYPE_PRODUCT = 2;

    public static final String CATEGORY_SERVICE = "SERVICE_CATEGORY";
    public static final String CATEGORY_PRODUCT = "PRODUCT_CATEGORY";

    public static final String PAYWAY_LEFTSUM = "LEFTSUM";
    public static final int DISCOUNT_TYPE_FIXED = 1;
    public static final float DISCOUNT_MIN = 0.5f;

    public static final String REGX_EMAIL = "^.+?@.+?\\.[a-zA-Z]{1,6}$";
    /**
     * 中国大陆常规手机号码与紧急号码，如110等
     */
    public static final String MOBILE_FMT_CHINA = "^((\\+?86)?1\\d{10}|(\\d{3}))$";
    /**
     * 国际手机号格式
     */
    public static final String MOBILE_FMT_I18N = "^\\+?\\d{1,16}$";
}
