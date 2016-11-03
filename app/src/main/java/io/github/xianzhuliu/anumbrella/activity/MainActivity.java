package io.github.xianzhuliu.anumbrella.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.City;
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
    private ProgressDialog progressDialog;
    private Weather weather;
    private String cityCode;
    private Toolbar toolbar;
    private List<MyCity> myCityList;
    private AnUmbrellaDB anUmbrellaDB;
    public static int selectedCity = 0;
    private AMapLocationClient mLocationClient;
    private AMapLocationListener mLocationListener;
    private AMapLocationClientOption mLocationOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        anUmbrellaDB = AnUmbrellaDB.getInstance(this);
        if (anUmbrellaDB.loadCities().isEmpty()) {
            cityInfoToSql();
        } else {
            if (true) {
                // 根据设置
                initAMap();
            }
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        tempText = (TextView) findViewById(R.id.temp);
        currentDateText = (TextView) findViewById(R.id.current_date);
        imgWeather = (ImageView) findViewById(R.id.img_weather);
    }

    private void initAMap() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        final String cityName = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        final City city = anUmbrellaDB.findCityFromLocation(cityName.substring(0, cityName.length() -
                                1), district.substring(0, district.length() - 1));
                        MyCity myCity = anUmbrellaDB.findMyCityById(1);
                        if (city != null && myCity.getCityId() != city.getId()) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("我猜你现在在" + city.getCountyName() + "~\n要关注" + city.getCountyName() +
                                    "的天气吗？");
                            dialog.setCancelable(false); // 通过back键取消
                            dialog.setPositiveButton("好好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    anUmbrellaDB.updateMyCity(1, city.getId());
                                    cityCode = city.getCityCode();
                                    queryWeatherInfo(cityCode);
                                }
                            });
                            dialog.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialog.show();
                        }
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void cityInfoToSql() {
        showProgressDialog();
        HttpUtil.getCitiesFromFile(MainActivity.this, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handleCitiesResponse(anUmbrellaDB, response);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            initAMap();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this, "城市信息加载失败，请尝试重新启动应用i_i", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("第一次需要加载城市信息\n请稍等...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myCityList = anUmbrellaDB.loadMyCities();
        if (myCityList.size() == 1 && myCityList.get(0).getCityId() == -1) {
            currentDateText.setText("请先添加城市");
            initDrawerLayout();
        } else {
            Log.d(TAG, "onStart: myCityid == " + myCityList.get(selectedCity).getId() + "\n cityId == " + myCityList
                    .get(selectedCity).getCityId());
            City city = anUmbrellaDB.findCityById(myCityList.get(selectedCity).getCityId());
            toolbar.setTitle(city.getCountyName());
            cityCode = city.getCityCode();
            queryWeatherInfo(cityCode);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.onDestroy();
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
        if (id == R.id.update_weather) {
            queryWeatherInfo(cityCode);
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
                boolean success = false;
                try {
                    success = Utility.handleWeatherResponse(MainActivity.this, new JSONObject(response), myCityList
                            .get(selectedCity).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    new RuntimeException("JSONException");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "貌似服务器出问题了~_~", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "更新失败，请检查网络后重试。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showWeather() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
        Gson gson = new Gson();
        weather = gson.fromJson(myCityList.get(selectedCity).getWeather(), Weather.class);
        if (weather == null) {
            queryWeatherInfo(cityCode);
        } else {
            tempText.setText(weather.daily_forecast.get(0).tmp.min + "°C ~ " + weather.daily_forecast.get(0).tmp.max
                    + "°C");
            weatherDespText.setText(weather.now.cond.txt + " " + weather.now.tmp + "°C");
            String publishTime = weather.basic.update.loc;
            publishText.setText(publishTime.substring(publishTime.length() - 11) + " 发布");
            imgWeather.setImageResource(WeatherCode.getWeatherCode(Integer.parseInt(weather.now.cond.code)));
            currentDateText.setText(simpleDateFormat.format(new Date()));
            weatherInfoLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "已更新天气 ^__^", Toast.LENGTH_SHORT).show();
        }
        initDrawerLayout();
        Intent service = new Intent(this, AutoUpdateService.class);
        //startService(service);
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

