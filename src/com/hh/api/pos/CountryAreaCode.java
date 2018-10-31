package com.hh.api.pos;

import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;

@Table(tableName = "country_area_code", dataSourceName = "pos", pkName = "code")
public class CountryAreaCode extends BaseModel<CountryAreaCode> {
    public static final CountryAreaCode dao = new CountryAreaCode().dao();

    private int code;
    private String en;
    private String cn;
    private String countryCode;
    private String countryEN;
    private String countryCN;
    private String provinceEN;
    private String provinceCN;
    private String parentEN;
    private String parentCN;
    private double lat;
    private double lng;
    private String adcode;

    public static final String column_code = "code";
    public static final String column_en = "en";
    public static final String column_cn = "cn";
    public static final String column_countryCode = "countryCode";
    public static final String column_countryEN = "countryEN";
    public static final String column_countryCN = "countryCN";
    public static final String column_provinceEN = "provinceEN";
    public static final String column_provinceCN = "provinceCN";
    public static final String column_parentEN = "parentEN";
    public static final String column_parentCN = "parentCN";
    public static final String column_lat = "lat";
    public static final String column_lng = "lng";
    public static final String column_adcode = "adcode";

    public int getCode() {
        return get(column_code);
    }

    public void setCode(int code) {
        this.set(column_code, code);
    }

    public String getEn() {
        return get(column_en);
    }

    public void setEn(String en) {
        this.set(column_en, en);
    }

    public String getCn() {
        return get(column_cn);
    }

    public void setCn(String cn) {
        this.set(column_cn, cn);
    }

    public String getCountryCode() {
        return get(column_countryCode);
    }

    public void setCountryCode(String countryCode) {
        this.set(column_countryCode, countryCode);
    }

    public String getCountryEN() {
        return get(column_countryEN);
    }

    public void setCountryEN(String countryEN) {
        this.set(column_countryEN, countryEN);
    }

    public String getCountryCN() {
        return get(column_countryCN);
    }

    public void setCountryCN(String countryCN) {
        this.set(column_countryCN, countryCN);
    }

    public String getProvinceEN() {
        return get(column_provinceEN);
    }

    public void setProvinceEN(String provinceEN) {
        this.set(column_provinceEN, provinceEN);
    }

    public String getProvinceCN() {
        return get(column_provinceCN);
    }

    public void setProvinceCN(String provinceCN) {
        this.set(column_provinceCN, provinceCN);
    }

    public String getParentEN() {
        return get(column_parentEN);
    }

    public void setParentEN(String parentEN) {
        this.set(column_parentEN, parentEN);
    }

    public String getParentCN() {
        return get(column_parentCN);
    }

    public void setParentCN(String parentCN) {
        this.set(column_parentCN, parentCN);
    }

    public double getLat() {
        return get(column_lat);
    }

    public void setLat(double lat) {
        this.set(column_lat, lat);
    }

    public double getLng() {
        return get(column_lng);
    }

    public void setLng(double lng) {
        this.set(column_lng, lng);
    }

    public String getAdcode() {
        return get(column_adcode);
    }

    public void setAdcode(String adcode) {
        this.set(column_adcode, adcode);
    }
}
