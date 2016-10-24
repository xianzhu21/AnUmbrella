package io.github.xianzhuliu.anumbrella.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.City;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class Utility {
    private static final String TAG = "Utility";

    public synchronized static boolean handleCitiesResponse(AnUmbrellaDB anUmbrellaDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(";");
            if (cities != null && cities.length > 0) {
                for (String s : cities) {
                    String[] array = s.split(",");
                    City city = new City();
                    city.setCityId(array[0]);
                    city.setCountyName(array[1]);
                    city.setCityName(array[2]);
                    city.setProvinceName(array[3]);
                    anUmbrellaDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject weatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
        if (weatherInfo.getString("status").equals("ok")) {
            String newDate = weatherInfo.getJSONObject("basic").getJSONObject("update").getString("loc");
            String cityId = weatherInfo.getJSONObject("basic").getString("id");

            boolean cached = true; // 是否存在缓存，true存在，false不存在或需要更新
            String path = context.getCacheDir() + "/" + cityId.substring(2, cityId.length());
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                cached = false;
            } else {
                String[] fileNames = dir.list();
                if (fileNames != null) {
                    String oldDate = fileNames[0];
                    if (!oldDate.equals(newDate)) { // 天气信息已更新
                        new File(dir, fileNames[0]).delete();
                        cached = false;
                    }
                }
            }

            if (!cached) {
                File file = new File(dir, newDate);
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
                writer.append(weatherInfo.toString());
                writer.close();
                fos.close();
            }
        } else {
            Log.e(TAG, "handleWeatherResponse: weatherInfo is error", new RuntimeException());
        }
    }
}
