package io.github.xianzhuliu.anumbrella.model;

/**
 * Created by LiuXianzhu on 27/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class MyCityBean {
    private String myCityName;
    private String myCityTmp;
    private int myCityWeatherCode;

    public int getMyCityWeatherCode() {
        return myCityWeatherCode;
    }

    public void setMyCityWeatherCode(int myCityWeatherCode) {
        this.myCityWeatherCode = myCityWeatherCode;
    }

    public String getMyCityTmp() {
        return myCityTmp;
    }

    public void setMyCityTmp(String myCityTmp) {
        this.myCityTmp = myCityTmp;
    }

    public String getMyCityName() {
        return myCityName;
    }

    public void setMyCityName(String myCityName) {
        this.myCityName = myCityName;
    }


    public MyCityBean(String myCityName, String myCityTmp, int myCityWeatherCode) {
        this.myCityName = myCityName;
        this.myCityTmp = myCityTmp;
        this.myCityWeatherCode = myCityWeatherCode;
    }
}
