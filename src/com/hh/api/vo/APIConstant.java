package com.hh.api.vo;

import com.platform.constant.ConstantWebContext;

/**
 * Created by Administrator on 2016/11/29.
 */
public interface APIConstant {
    public static final String DEFAULT_BLANK = "";
    public static final String DEFAULT_SPACE = " ";
    public static final Object DEFAULT_OBJECT = new Object();

    String ACCESS_KEY = ConstantWebContext.cookie_authmark;
    String ACCESS_UID = "uid";
    String LOCALE = "locale";
    String VERSION_CODE = "code";
    String PREFIX_TIME="TIME_";
    String PREFIX_COUNT="COUNT_";
    String PREFIX_PIN="PIN_";
    public static final String UPLOAD_FILE_PATH="files/upload";
}
