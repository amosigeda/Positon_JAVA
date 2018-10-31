package com.hh.api.tools;


import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.text.ParseException;
import java.util.*;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.hh.mvc.vo.Constants;

/**
 * Created by HH
 * Date: 2015/6/11 0011
 * Time: 下午 13:56
 */
public class CommUtil {


    public static final Logger LOGGER = LoggerFactory.getLogger(CommUtil.class);

    /**
     * 对象到字符串
     *
     * @param obj
     * @return 字符串，默认为""
     */
    public static String toStr(Object obj) {
        return toStr(obj, "");
    }

    /**
     * 对象到字符串
     *
     * @param obj
     * @param defaultVal
     * @return 字符串，默认为 defaultVal 的值
     */
    public static String toStr(Object obj, String defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else if (obj instanceof byte[]) {
            return new String((byte[]) obj);
        } else {
            return obj.toString();
        }
    }

    public static boolean isAllNotBlank(String... fields) {
        if (fields == null) {
            return false;
        }
        for (String str : fields) {
            if (StringUtils.isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean isBlank(Object object) {
        if (object == null) {
            return true;
        }
        int strLen;
        String str = object.toString();
        if ((strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(Object object) {
        return !isBlank(object);
    }

    /**
     * 获取时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDateTime() {
        return Constants.COMM_DATETIME_FMT.format(new Date());
    }

    /**
     * 获取时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateTime(Date date) {
        return Constants.COMM_DATETIME_FMT.format(date);
    }

    /**
     * 获取日期 格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getDate() {
        return Constants.COMM_DATE_FMT.format(new Date());
    }

    /**
     * 获取日期  格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getDate(Date date) {
        if (date == null) {
            return null;
        }
        return Constants.COMM_DATE_FMT.format(date);
    }

    /**
     * 将字符串时间转换成Date类型（yyyy-MM-dd HH:mm:ss） String dateTimeStr --日期对象的字符串
     *
     * @param dateTimeStr
     * @return
     * @throws ParseException
     */
    public static Date getDateTimeFromStr(String dateTimeStr)
            throws ParseException {
        return Constants.COMM_DATETIME_FMT.parse(dateTimeStr);
    }

    /**
     * 将字符串时间转换成Date类型（yyyy-MM-dd） String dateStr --日期对象的字符串
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date getDateFromStr(String dateStr) throws ParseException {
        return Constants.COMM_DATE_FMT.parse(dateStr);
    }

    public static long getCapacitySize(String sizeStr) {
        int offset = 0;
        if (sizeStr.charAt(sizeStr.length() - 1) == 'B') {
            offset = 1;
        }
        char unit = sizeStr.charAt(sizeStr.length() - 1 - offset);
        if (unit >= '0' && unit <= '9') {
            return toLong(sizeStr);
        }
        long digit = toLong(sizeStr.substring(0, sizeStr.length() - 1 - offset));
        switch (unit) {
            case 'k':
            case 'K':
                return digit << 10;
            case 'm':
            case 'M':
                return digit << 20;
            case 'g':
            case 'G':
                return digit << 30;
            case 't':
            case 'T':
                return digit << 40;
            case 'p':
            case 'P':
                return digit << 50;
            default:
                return digit;
        }
    }

    /**
     * 获取baseUrl，结尾不带斜杠
     *
     * @param url
     * @return
     */
    public static String getBaseUrl(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            URI uri = new URI(url);
            sb.append(uri.getScheme()).append("://").append(uri.getHost());
            if (uri.getPort() != 80 && uri.getPort() != -1) {
                sb.append(':').append(uri.getPort());
            }
        } catch (Exception e) {
            LOGGER.error("格式化URL出错，原URL：{}", url, e);
        }
        return sb.toString();
    }

    /**
     * 返回MD5加密的字符串
     *
     * @param str
     * @return
     */
    public static String getMD5Plus(String str) {
        if (str == null) {
            return null;
        }
        int i = Math.abs(str.hashCode()) % Constants.MD5_SALTS.length;
        return Md5Crypt.md5Crypt(str.getBytes(), Constants.MD5_SALTS[i]);
    }

    public static Integer toInteger(Object obj) {
        return toInteger(obj, null);
    }

    public static Integer toInteger(Object obj, Integer defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(toStr(obj));
        } catch (Exception e) {
            LOGGER.error("Parse obj to Integer error!", e);
            return defaultVal;
        }
    }

    public static Long toLong(Object obj) {
        return toLong(obj, null);
    }

    public static Long toLong(Object obj, Long defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        try {
            return Long.parseLong(toStr(obj));
        } catch (Exception e) {
            LOGGER.error("Parse obj to Long error!", e);
            return defaultVal;
        }
    }

    public static Double toDouble(Object obj) {
        return toDouble(obj, null);
    }

    public static Double toDouble(Object obj, Double defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(toStr(obj));
        } catch (Exception e) {
            LOGGER.error("Parse obj to Long error!", e);
            return defaultVal;
        }
    }

    public static Float toFloat(Object obj) {
        return toFloat(obj, null);
    }

    public static Float toFloat(Object obj, Float defaultVal) {
        if (obj == null) {
            return defaultVal;
        }
        try {
            return Float.parseFloat(toStr(obj));
        } catch (Exception e) {
            LOGGER.error("Parse obj to Long error!", e);
            return defaultVal;
        }
    }

    public static boolean toBoolean(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue() != 0D;
        }
        String res = obj.toString();
        return !"NULL".equalsIgnoreCase(res) && !"0".equals(res) && StringUtils.isNotBlank(res) && !"FALSE".equalsIgnoreCase(res);
    }

    /**
     * 将以米数为单位的距离转换成纬度度数
     * (避免用Double初始化BigDecimal类型)
     *
     * @param metre
     * @return
     */
    public static double metreToLonLatDegree(Double metre) {
        return new BigDecimal(toStr(metre)).multiply(new BigDecimal(180)).divide(new BigDecimal(toStr(Math.PI))
                .multiply(new BigDecimal(toStr(Constants.EARTH_RADIUS))), 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return 返回距离 单位：米
     */
    public static double lonLanToDistance(double long1, double lat1, double long2,
                                          double lat2) {
        double a, b, R = Constants.EARTH_RADIUS;
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public static List<Integer> upcaseIndex(String word) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    public static String convert2SqlField(String fieldName) {
        List<Integer> integerList = upcaseIndex(fieldName);
        StringBuilder sb = new StringBuilder();
        if (integerList.size() > 0) {
            int pos = 0;
            for (Integer index : integerList) {
                sb.append(fieldName.substring(pos, index)).append("_");
                pos = index;
            }
            sb.append(fieldName.substring(pos));
        } else {
            return fieldName;
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 根据IMEI号生成固定位数
     *
     * @param imei
     * @return
     */
    public static String generateValicode(String imei) {
        char[] defaultChs = {'2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r',
                's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        String sys = "23m4iy8hgvjteux5d7p9nzf6qabwcksr";
        char[] chs = sys != null ? sys.toCharArray() : defaultChs;
        int imeiLen = imei.length();
        StringBuilder sb = new StringBuilder();
        char start = imei.charAt(6);
        char mid = imei.charAt(7);
        char end = imei.charAt(8);
        for (int i = 0; i < 6; i++) {
            Integer val = ((imei.charAt(i) + imei.charAt(imeiLen - 1 - i) + start + end)) * mid * (i + 1);
            int result = 31 * Integer.parseInt(imei.substring(10)) + (val.hashCode());
            result = result % chs.length;
            String ch = String.valueOf(chs[result]);
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 暂未使用
     *
     * @param imei
     * @param len
     * @return
     */
    public static String generateValicode(String imei, int len) {
        int imeiLen = imei.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            int val = imei.charAt(i) + imei.charAt(imeiLen - 1 - i);
            val = (val >>> i & 0x0F);
            sb.append(Integer.toHexString(val));
        }
        sb.append(imei.substring(7, 8));
        return generateUniqueId(sb.toString(), 6).toLowerCase();
    }

    /**
     * 创建一定位数的唯一id
     * 一般用了根据id生成imei号
     * 这个方法必须与APP上的同一方法保持一致
     *
     * @param seed
     * @param len
     * @return
     */
    public static String generateUniqueId(Object seed, int len) {
        if (seed == null) {
            return null;
        }
        if (len < 0 || len > 50) {
            len = 15;
        }
        String code;
        String sd = seed.toString();
        if (!sd.matches("^\\d+$")) {
            code = String.valueOf(Math.abs(sd.hashCode()));
        } else {
            code = String.valueOf(Math.abs(Long.parseLong(sd)));
        }
        StringBuffer sb = new StringBuffer().append(code).append(code.length());
        int h = 0;
        while (sb.length() < len) {
            for (int i = 0; i < sb.length(); i++) {
                h = 31 * h + sb.charAt(i);
            }
            sb.append(Math.abs(h));
        }
        return sb.substring(0, len);
    }

    public static String genIMEI(Object uniqueId) {
        return generateUniqueId(uniqueId, 15);
    }

    /**
     * 每次随机分配当前月份下不同的目录，最多10个
     *
     * @param basePath
     * @return
     */
    public static String genAvgPathByMonth(String basePath) {
        return genAvgPathByMonth(basePath, 10);
    }

    private static String genAvgPathByMonth(String basePath, int maxNum) {
        Calendar ca = Calendar.getInstance();
        StringBuilder sb = new StringBuilder(basePath);
        if (!basePath.endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(ca.get(Calendar.MONTH)).append(File.separator).append((int) (ca.getTimeInMillis() % maxNum)).append(File.separator);
        return sb.toString();
    }

    /**
     * 获取拼音
     *
     * @param w         待获取拼音的字符串
     * @param separator 分隔符
     * @return
     */
    public static String getPinYin(String w, String separator) {
        try {
            if (separator == null) {
                separator = "";
            }
            return PinyinHelper.convertToPinyinString(w, separator, PinyinFormat.WITHOUT_TONE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getShortPinYin(String w) {
        try {
            return PinyinHelper.getShortPinyin(w);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int min(int... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        int res = args[0];
        for (int i = 1; i < args.length; i++) {
            if (res > args[i]) {
                res = args[i];
            }
        }
        return res;
    }

    public static String join(Object[] objs, String seprator) {
        if (objs == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objs.length; i++) {
            if (i > 0) {
                sb.append(seprator);
            }
            sb.append(objs[i]);
        }
        return sb.toString();
    }

}
