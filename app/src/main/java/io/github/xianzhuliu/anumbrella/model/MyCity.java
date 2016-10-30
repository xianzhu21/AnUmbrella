package io.github.xianzhuliu.anumbrella.model;

/**
 * Created by LiuXianzhu on 27/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class MyCity {
    private int id;
    private int cityId;
    private String weather;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
