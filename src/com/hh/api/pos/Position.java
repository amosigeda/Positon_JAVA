package com.hh.api.pos;

import java.io.Serializable;

/**
 * Created by Steven Hua on 2017/5/19.
 */

public class Position implements Serializable {
    private Double lat;
    private Double lng;
    private Integer radius;
    private String address;
    private String roads;
    private Double lata;
    private Double lnga;

    public Position(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoads() {
        return roads;
    }

    public void setRoads(String roads) {
        this.roads = roads;
    }

    public Double getLata() {
        return lata;
    }

    public void setLata(Double lata) {
        this.lata = lata;
    }

    public Double getLnga() {
        return lnga;
    }

    public void setLnga(Double lnga) {
        this.lnga = lnga;
    }

    @Override
    public String toString() {
        return "Position{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", radius=" + radius +
                ", address='" + address + '\'' +
                ", roads='" + roads + '\'' +
                ", lata=" + lata +
                ", lnga=" + lnga +
                '}';
    }
}
