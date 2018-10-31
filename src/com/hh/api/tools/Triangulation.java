package com.hh.api.tools;

import java.awt.geom.Point2D;
import java.lang.*;

public class Triangulation {
    private int max_location;
    final static double PI = 3.14159265359;
    private int count;
    private Point2D[] coord;  //坐标
    private int[] rxLevel;    //信号强度，单位是dbm，是负值

    public boolean add(Point2D coord, int rxLevel) {
        if (count >= max_location) {
            return false;
        }
        this.coord[count] = coord;
        this.rxLevel[count] = rxLevel;
        count++;
        return true;
    }

    public Triangulation(int max_location) {
        this.max_location = max_location;
        coord = new Point2D[max_location];
        rxLevel = new int[max_location];
    }

    public Point2D location() {
        double[] distance_weight = new double[max_location];  //位置权重
        double distance_sum = 0.0;
        double[] lat = new double[max_location];  //纬度
        double[] lon = new double[max_location];  //经度
        double signalLevel;
        double x, y, z;
        double x_avg, y_avg, z_avg;
        double lat_avg, lon_avg;
        double freq = 1000;  //接收频率设为1000Mhz，如果你的设备能获取到实际的接收频率，可以修改这个参数

        for (int i = 0; i < count; i++) {
            lat[i] = coord[i].getY() / 180.0 * PI;  //转换为弧度单位
            lon[i] = coord[i].getX() / 180.0 * PI;  //转换为弧度单位
            signalLevel = this.rxLevel[i];
            //根据Free-space path loss公式，计算设备与基站距离作为位置权重
            distance_weight[i] = Math.pow(10.0, (130.0 + signalLevel - 20.0 * Math.log10(freq)) / 20.0);
            distance_sum += distance_weight[i];
        }

        x = 0.0;
        y = 0.0;
        z = 0.0;

        //转换为球面坐标
        //根据位置权重计算平均位置
        for (int i = 0; i < count; i++) {
            x += Math.cos(lat[i]) * Math.cos(lon[i]) * distance_weight[i];
            y += Math.cos(lat[i]) * Math.sin(lon[i]) * distance_weight[i];
            z += Math.sin(lat[i]) * distance_weight[i];
        }

        x_avg = x / distance_sum;
        y_avg = y / distance_sum;
        z_avg = z / distance_sum;

        //转换为经纬度坐标
        lat_avg = Math.atan(z_avg / Math.sqrt(x_avg * x_avg + y_avg * y_avg)) * 180.0 / PI;
        lon_avg = Math.atan(y_avg / x_avg) * 180.0 / PI;

        if (lon_avg < 0) lon_avg += 180.0;

        return new Point2D.Double(lon_avg, lat_avg);
    }

    public static void main(String[] args) {
        Triangulation t = new Triangulation(4);
        t.add(new Point2D.Double(116.0, 40.0), -100);
        t.add(new Point2D.Double(115.0, 39.0), -60);
        t.add(new Point2D.Double(115.0, 40.0), -80);
        t.add(new Point2D.Double(116.0, 39.0), -75);
        Point2D result = t.location();
        System.out.println(result.getX() + "," + result.getY());
    }
}
