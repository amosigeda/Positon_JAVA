package com.hh.api.pos;

import java.math.BigDecimal;

import com.jfinal.plugin.activerecord.Model;
import com.platform.mvc.base.BaseModel;

/**
 * Created by Steven Hua on 2017/5/19.
 */

public abstract class PositionBase<T extends Model<T>> extends BaseModel<T> {
    private String ID;
    private Integer MCC;
    private Integer Ver;
    private Integer Radius;
    private String Address;
    private BigDecimal Lat;
    private BigDecimal Lng;
    private Integer REF;

    public static final String column_ID = "ID";
    public static final String column_MCC = "MCC";
    public static final String column_Ver = "Ver";
    public static final String column_Radius = "Radius";
    public static final String column_Address = "Address";
    public static final String column_Lat = "Lat";
    public static final String column_Lng = "Lng";
    public static final String column_REF = "REF";


    public String getID() {
        return get(column_ID);
    }

    public void setID(String ID) {
        this.set(column_ID, ID);
    }

    public Integer getMCC() {
        return get(column_MCC);
    }

    public void setMCC(Integer MCC) {
        this.set(column_MCC, MCC);
    }

    public Integer getVer() {
        return get(column_Ver);
    }

    public void setVer(Integer ver) {
        set(column_Ver, ver);
    }

    public Integer getRadius() {
        return get(column_Radius);
    }

    public void setRadius(Integer radius) {
        set(column_Radius, radius);
    }

    public String getAddress() {
        return get(column_Address);
    }

    public void setAddress(String address) {
        set(column_Address, address);
    }

    public BigDecimal getLat() {
        return get(column_Lat);
    }

    public void setLat(BigDecimal lat) {
        set(column_Lat, lat);
    }

    public BigDecimal getLng() {
        return get(column_Lng);
    }

    public void setLng(BigDecimal lng) {
        set(column_Lng, lng);
    }

    public Integer getREF() {
        return get(column_REF);
    }

    public void setREF(Integer REF) {
        this.set(column_REF, REF);
    }

    @Override
    public String toString() {
        return "PositionBase{" +
                "ID='" + getID() + '\'' +
                ", MCC=" + getMCC() +
                ", Ver=" + getVer() +
                ", Radius=" + getRadius() +
                ", Address='" + getAddress() + '\'' +
                ", Lat=" + getLat() +
                ", Lng=" + getLng() +
                ", REF=" + getREF() +
                '}';
    }
}
