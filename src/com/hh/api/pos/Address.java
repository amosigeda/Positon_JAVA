package com.hh.api.pos;

import java.util.ArrayList;
import java.util.List;

public class Address {
    private String province;
    private String city;
    private String district;
    private String road;
    private List<String> nearby = new ArrayList<>();

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public List<String> getNearby() {
        return nearby;
    }

    public void setNearby(List<String> nearby) {
        this.nearby = nearby;
    }

    @Override
    public String toString() {
        return "Address{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", road='" + road + '\'' +
                ", nearby=" + nearby +
                '}';
    }
}
