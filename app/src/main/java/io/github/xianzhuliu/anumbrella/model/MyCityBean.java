package io.github.xianzhuliu.anumbrella.model;

import io.github.xianzhuliu.anumbrella.R;

/**
 * Created by LiuXianzhu on 27/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class MyCityBean {
    private String myCityName;
    private String myCityTmp;

    public int getMyCityImgResId() {
        return R.drawable.weather_100;
    }

    public void setMyCityImgResId(int myCityImgResId) {
        this.myCityImgResId = myCityImgResId;
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

    private int myCityImgResId;

    public MyCityBean(String myCityName, String myCityTmp, int myCityImgResId) {
        this.myCityName = myCityName;
        this.myCityTmp = myCityTmp;
        this.myCityImgResId = myCityImgResId;
    }
}
