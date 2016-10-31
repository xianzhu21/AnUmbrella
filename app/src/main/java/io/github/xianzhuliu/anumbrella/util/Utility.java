package io.github.xianzhuliu.anumbrella.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                    city.setCityCode("CN" + array[0]);
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

    public static boolean handleWeatherResponse(Context context, JSONObject response, int myCityId) throws
            JSONException, IOException {
        JSONObject weatherInfo = response.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
        if (weatherInfo.getString("status").equals("ok")) {
            AnUmbrellaDB anUmbrellaDB = AnUmbrellaDB.getInstance(context);
            anUmbrellaDB.updateWeather(myCityId, weatherInfo.toString());
            return true;
        } else {
            return false;
        }
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
