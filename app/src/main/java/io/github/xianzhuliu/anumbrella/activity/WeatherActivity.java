package io.github.xianzhuliu.anumbrella.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.model.Weather;
import io.github.xianzhuliu.anumbrella.service.AutoUpdateService;
import io.github.xianzhuliu.anumbrella.util.HttpCallbackListener;
import io.github.xianzhuliu.anumbrella.util.HttpUtil;
import io.github.xianzhuliu.anumbrella.util.Utility;

/**
 * Created by LiuXianzhu on 20/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class WeatherActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "WeatherActivity";
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;
    private Weather weather;
    private String cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

        cityId = getIntent().getStringExtra("city_id");
        if (!TextUtils.isEmpty(cityId)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(cityId);
        } else {
            showWeather();
        }
    }

    private void queryWeatherInfo(String cityId) {
        String address = "https://api.heweather.com/x3/weather?cityid=CN" + cityId +
                "&key=b722b324cb4a43c39bd1ca487cc89d7c";
        queryFromServer(address);
    }

    private void queryFromServer(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    new RuntimeException("JSONException");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
        File[] files = new File(getCacheDir() + "/" + cityId).listFiles();
        if (files == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            cityId = prefs.getString("city_id", null);
            if (TextUtils.isEmpty(cityId)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("city_selected", false);
                editor.apply();
                Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            queryWeatherInfo(cityId);
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(files[0]);
                InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8");
                Gson gson = new Gson();
                weather = gson.fromJson(reader, Weather.class);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            cityNameText.setText(weather.basic.city);
            temp1Text.setText(weather.daily_forecast.get(0).tmp.min + "°C");
            temp2Text.setText(weather.daily_forecast.get(0).tmp.max + "°C");
            weatherDespText.setText(weather.now.cond.txt + " " + weather.now.tmp + "°C");
            publishText.setText(weather.basic.update.loc + "发布");
            currentDateText.setText(simpleDateFormat.format(new Date()));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent service = new Intent(this, AutoUpdateService.class);
        startService(service);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                queryWeatherInfo(cityId);
                break;
            default:
                break;
        }
    }
}
