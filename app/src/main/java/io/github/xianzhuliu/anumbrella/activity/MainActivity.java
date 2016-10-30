package io.github.xianzhuliu.anumbrella.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.MyCity;
import io.github.xianzhuliu.anumbrella.model.Weather;
import io.github.xianzhuliu.anumbrella.service.AutoUpdateService;
import io.github.xianzhuliu.anumbrella.util.HttpCallbackListener;
import io.github.xianzhuliu.anumbrella.util.HttpUtil;
import io.github.xianzhuliu.anumbrella.util.Utility;
import io.github.xianzhuliu.anumbrella.util.WeatherCode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private LinearLayout weatherInfoLayout;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView tempText;
    private TextView currentDateText;
    private ImageView imgWeather;
    private Weather weather;
    private String cityCode;
    private Toolbar toolbar;
    private List<MyCity> myCityList;
    private AnUmbrellaDB anUmbrellaDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        tempText = (TextView) findViewById(R.id.temp);
        currentDateText = (TextView) findViewById(R.id.current_date);
        imgWeather = (ImageView) findViewById(R.id.img_weather);

        anUmbrellaDB = AnUmbrellaDB.getInstance(this);
        myCityList = anUmbrellaDB.loadMyCities();
        if (myCityList.isEmpty()) {
            weatherDespText.setText("请先添加城市。");
        } else {
            showWeather();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update_weather) {
            queryWeatherInfo(cityCode);
            Toast.makeText(this, "已更新天气 ^__^", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_city) {
            Intent starter = new Intent(MainActivity.this, CityActivity.class);
            startActivity(starter);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void queryWeatherInfo(String cityCode) {
        String address = "https://api.heweather.com/x3/weather?cityid=" + cityCode +
                "&key=b722b324cb4a43c39bd1ca487cc89d7c";
        HttpUtil.sendOkHttp(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    Utility.handleWeatherResponse(MainActivity.this, new JSONObject(response), myCityList.get(0)
                            .getId());
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
                        publishText.setText("更新失败，请检查网络后重试。");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
        Gson gson = new Gson();
        weather = gson.fromJson(myCityList.get(0).getWeather(), Weather.class);
        toolbar.setTitle(weather.basic.city);
        tempText.setText(weather.daily_forecast.get(0).tmp.min + "°C ~ " + weather.daily_forecast.get(0).tmp.max +
                "°C");
        weatherDespText.setText(weather.now.cond.txt + " " + weather.now.tmp + "°C");
        publishText.setText(weather.basic.update.loc + "发布");
        imgWeather.setImageResource(WeatherCode.getWeatherCode(Integer.parseInt(weather.now.cond.code)));
        currentDateText.setText(simpleDateFormat.format(new Date()));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityCode = weather.basic.id;
        initDrawerLayout();
        Intent service = new Intent(this, AutoUpdateService.class);
        startService(service);
    }

    private void initDrawerLayout() {
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }
}

