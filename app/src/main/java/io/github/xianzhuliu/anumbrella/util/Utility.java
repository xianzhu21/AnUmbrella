package io.github.xianzhuliu.anumbrella.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public static void handleWeatherResponse(Context context, JSONObject response) throws JSONException, IOException {
        JSONObject weatherInfo = response.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
        if (weatherInfo.getString("status").equals("ok")) {
            String newDate = weatherInfo.getJSONObject("basic").getJSONObject("update").getString("loc");
            String cityId = weatherInfo.getJSONObject("basic").getString("id");
            final String condCode = weatherInfo.getJSONObject("now").getJSONObject("cond").getString("code");

            final String imgPath = context.getCacheDir().getAbsolutePath() + "/condImg";
            if (!new File(imgPath, condCode + ".png").exists()) {
                String condUrl = "http://files.heweather.com/cond_icon/" + condCode + ".png";

                RequestQueue queue = Volley.newRequestQueue(context);
                ImageRequest imageRequest = new ImageRequest(condUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        File dir = new File(imgPath);
                        dir.mkdirs();
                        File img = new File(dir, condCode + ".png");
                        try {
                            OutputStream outputStream = new FileOutputStream(img);
                            response.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(imageRequest);
            }
            boolean cached = true; // 是否存在缓存，true存在，false不存在或需要更新
            String path = context.getCacheDir().getAbsolutePath() + "/" + cityId.substring(2, cityId.length());
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
