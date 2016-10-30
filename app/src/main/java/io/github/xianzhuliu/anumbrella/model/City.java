package io.github.xianzhuliu.anumbrella.model;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class City {
    private int id;
    private String provinceName;
    private String cityName;
    private String countyName;
    private String cityCode;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

}
