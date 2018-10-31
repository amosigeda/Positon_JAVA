package com.hh.api.pos;

public class Weather {
    private String province;
    private String city;
    private String cityName;
    private String cityCode;
    private String weather;
    private int temp;
    private String wind;
    private String windGrade;
    private String pm25;
    private String humidity;

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

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWindGrade() {
        return windGrade;
    }

    public void setWindGrade(String windGrade) {
        this.windGrade = windGrade;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityName='" + cityName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", weather='" + weather + '\'' +
                ", temp=" + temp +
                ", wind='" + wind + '\'' +
                ", windGrade='" + windGrade + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", humidity='" + humidity + '\'' +
                '}';
    }
}
