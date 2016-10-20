package io.github.xianzhuliu.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.xianzhuliu.coolweather.db.CoolWeatherDB;
import io.github.xianzhuliu.coolweather.model.Location;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class Utility {
    public synchronized static boolean handleLocationsResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] locations = response.split(";");
            if (locations != null && locations.length > 0) {
                for (String s : locations) {
                    if (!s.isEmpty()) {
                        String[] array = s.split(",");
                        Location location = new Location();
                        location.setLocationCode(array[0]);
                        location.setCountyName(array[1]);
                        location.setCityName(array[2]);
                        location.setProvinceName(array[3]);
                        coolWeatherDB.saveLocation(location);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
        String cityName = weatherInfo.getString("city");
        String locationCode = weatherInfo.getString("cityid");
        String temp1 = weatherInfo.getString("temp1");
        String temp2 = weatherInfo.getString("temp2");
        String weatherDesp = weatherInfo.getString("weather");
        String publishTime = weatherInfo.getString("ptime");
        saveWeatherInfo(context, cityName, locationCode, temp1, temp2, weatherDesp, publishTime);
    }

    public static void saveWeatherInfo(Context context, String cityName, String locationCode, String temp1, String
            temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("location_code", locationCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.apply();
    }
}
