package io.github.xianzhuliu.anumbrella.util;

import io.github.xianzhuliu.anumbrella.R;

/**
 * Created by LiuXianzhu on 28/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class WeatherCode {
    public static int getWeatherCode(int code) {
        switch (code) {
            case 100:
                return R.drawable.weather_100;
            default:
                return R.drawable.weather_100;
        }
    }
}
