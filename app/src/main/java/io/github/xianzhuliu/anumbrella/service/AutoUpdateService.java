package io.github.xianzhuliu.anumbrella.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.MyCity;
import io.github.xianzhuliu.anumbrella.model.Weather;
import io.github.xianzhuliu.anumbrella.util.HttpCallbackListener;
import io.github.xianzhuliu.anumbrella.util.HttpUtil;
import io.github.xianzhuliu.anumbrella.util.Utility;

/**
 * Created by LiuXianzhu on 21/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        AnUmbrellaDB anUmbrellaDB = AnUmbrellaDB.getInstance(this);
        List<MyCity> myCityList = anUmbrellaDB.loadMyCities();
        for (final MyCity myCity : myCityList) {
            Gson gson = new Gson();
            Weather weather = gson.fromJson(myCity.getWeather(), Weather.class);

            String cityCode = anUmbrellaDB.findCityById(myCity.getCityId()).getCityCode();
            String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode +
                    "&key=b722b324cb4a43c39bd1ca487cc89d7c";
            HttpUtil.sendOkHttp(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        if (!Utility.handleWeatherResponse(AutoUpdateService.this, new JSONObject(response), myCity
                                .getId())) {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        new RuntimeException("JSONException");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AutoUpdateService.this, "更新失败，请检查网络后重试。", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
