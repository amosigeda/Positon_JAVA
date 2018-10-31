package com.hh.api.tools;



public class CoordinateUtil {
    private final static double PI = 3.14159265358979324;
    private final static double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private final static double A = 6378245.0;
    private final static double EE = 0.00669342162296594323;
    private final static double EARTH_RADIUS = 6378.137;//地球半径

    /**
     * GCJ-02 坐标转换成 BD-09
     *
     * @param gg_lat
     * @param gg_lon
     * @return
     */
    public static double[] GCJToBD09(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lat, bd_lon};
    }

    /**
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    public static double[] BD09ToGCJ(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lat, gg_lon};
    }

    /**
     * WGS-84 转 GCJ-02
     *
     * @param wgLat
     * @param wgLon
     */
    public static double[] WGS84ToGCJ(double wgLat, double wgLon) {
        double gcjLat, gcjLon;
        if (OutOfChina(wgLat, wgLon)) {
            gcjLat = wgLat;
            gcjLon = wgLon;
        } else {
            double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
            double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
            double radLat = wgLat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
            gcjLat = wgLat + dLat;
            gcjLon = wgLon + dLon;
        }
        return new double[]{gcjLat, gcjLon};
    }

    public static double[] GCJToWGS84(double gcjLat, double gcjLon) {
        double wgLat, wgLon;
        if (OutOfChina(gcjLat, gcjLon)) {
            wgLat = gcjLat;
            wgLon = gcjLon;
        } else {
            double dLat = transformLat(gcjLon - 105.0, gcjLat - 35.0);
            double dLon = transformLon(gcjLon - 105.0, gcjLat - 35.0);
            double radLat = gcjLat / 180.0 * PI;
            double magic = Math.sin(radLat);
            magic = 1 - EE * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
            dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
            wgLat = gcjLat - dLat;
            wgLon = gcjLon - dLon;
        }
        return new double[]{wgLat, wgLon};
    }

    private static boolean OutOfChina(double lat, double lon) {
        return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    public static double[] WGS84ToBD09(double wgLat, double wgLon) {
        double[] coord = WGS84ToGCJ(wgLat, wgLon);
        return GCJToBD09(coord[0], coord[1]);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 亮点间距离计算
     *
     * @param lat1 开始纬度
     * @param lng1 开始经度
     * @param lat2 结束纬度
     * @param lng2 结束经度
     * @return 单位米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 100000) / 100;
        return s;
    }

    /**
     * 判断是否在区域内
     * @param lat 经度
     * @param lng 纬度
     * @param pts 区域点数组
     * @return
     */
    public static boolean polygonIsInZone(double lat, double lng, String pts) {
        boolean inZone = false;
        int j = 0;

        String[] points = pts.split("\\|");

        for (String Point : points) {
            j++;
            if (j == points.length) {
                j = 0;
            }

            String[] pti = Point.split(",");
            String[] ptj = points[j].split(",");
            double ptiLat = CommUtil.toDouble(pti[0], 0d);
            double ptiLng = CommUtil.toDouble(pti[1], 0d);
            double ptjLat = CommUtil.toDouble(ptj[0], 0d);
            double ptjLng = CommUtil.toDouble(ptj[1], 0d);

            if (((ptiLat < lat) && (ptjLat >= lat)) || ((ptjLat < lat) && (ptiLat >= lat))) {
                if (ptiLng + (lat - ptiLat) / (ptjLat - ptiLat) * (ptjLng - ptiLng) < lng) {
                    inZone = !inZone;
                }
            }

        }
        return inZone;
    }
}
