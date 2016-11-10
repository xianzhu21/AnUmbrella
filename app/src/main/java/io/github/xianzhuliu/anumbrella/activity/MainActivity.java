package io.github.xianzhuliu.anumbrella.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.adapter.WeatherPagerAdapter;
import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.City;
import io.github.xianzhuliu.anumbrella.model.MyCity;
import io.github.xianzhuliu.anumbrella.util.HttpCallbackListener;
import io.github.xianzhuliu.anumbrella.util.HttpUtil;
import io.github.xianzhuliu.anumbrella.util.Utility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WeatherFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;
    public static boolean sUpdateMyCity = false;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<WeatherFragment> mWeatherFragmentList;
    private List<MyCity> mMyCityList;
    private AnUmbrellaDB mAnUmbrellaDB;
    private AMapLocationClient mLocationClient;
    private AMapLocationListener mLocationListener;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mWeatherFragmentList = new ArrayList<>();
        mAnUmbrellaDB = AnUmbrellaDB.getInstance(getApplicationContext());
        if (mAnUmbrellaDB.loadCities().isEmpty()) {
            cityInfoToSql();
        } else {
            if (true) {
                // 根据设置
                initAMap();
            }
            initViewPager();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sUpdateMyCity) {
            updateFragment();
            mPagerAdapter.notifyDataSetChanged();
            Log.d(TAG, "onStart: page count====" + mPagerAdapter.getCount());
        }
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mPager = (ViewPager) findViewById(R.id.pager);
    }

    private void updateFragment() {
        mMyCityList = mAnUmbrellaDB.loadMyCities();
        if (mMyCityList.get(0).getCityId() == -1) {
            mMyCityList.remove(0);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (Fragment fragment : fragments) {
                if (fragment != null) { // 否则NullPointerException
                    transaction.remove(fragment);
                } else {
                    if (DEBUG)
                        Log.v(TAG, "updateFragment: fragment is null");
                }
            }
            transaction.commit();
        }
        mWeatherFragmentList.clear();
        if (mMyCityList.size() == 0) {
            initDrawerLayout("请先添加城市");
        } else {
            for (MyCity myCity : mMyCityList) {
                WeatherFragment fragment = WeatherFragment.newInstance(myCity.getId());
                mWeatherFragmentList.add(fragment);
            }
            initDrawerLayout(mAnUmbrellaDB.findCityById(mMyCityList.get(mPager.getCurrentItem()).getCityId())
                    .getCountyName());
        }
    }

    private void initViewPager() {
        updateFragment();
        mPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), mWeatherFragmentList);

        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyCity myCityNow = mMyCityList.get(position);
                City city = mAnUmbrellaDB.findCityById(myCityNow.getCityId());
                initDrawerLayout(city.getCountyName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initAMap() {
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        final String cityName = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        final City city = mAnUmbrellaDB.findCityFromLocation(cityName.substring(0, cityName.length() -
                                1), district.substring(0, district.length() - 1));
                        final MyCity myCity = mAnUmbrellaDB.findMyCityById(1);
                        if (city != null && myCity.getCityId() != city.getId()) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("我猜你现在在" + city.getCountyName() + "~\n要关注" + city.getCountyName() +
                                    "的天气吗？");
                            dialog.setCancelable(false); // 通过back键取消
                            dialog.setPositiveButton("好好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mAnUmbrellaDB.updateMyCity(1, city.getId());
                                    // 先查询天气再添加fragment
                                    synchronized (this) {
                                        queryWeatherInfo(city.getCityCode(), 1);
                                        notifyAll();
                                    }
                                    synchronized (this) {
                                        while (myCity.getWeather() == null) {
                                            try {
                                                wait();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        updateFragment();
                                        mPagerAdapter.notifyDataSetChanged();
                                        mPager.setCurrentItem(0);
                                    }
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
            for (MyCity myCity : mMyCityList) {
                City city = mAnUmbrellaDB.findCityById(myCity.getCityId());
                queryWeatherInfo(city.getCityCode(), myCity.getId());
            }
            mPagerAdapter.notifyDataSetChanged();
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

    private void queryWeatherInfo(String cityCode, final int myCityId) {
        String address = "https://free-api.heweather.com/x3/weather?cityid=" + cityCode +
                "&key=b722b324cb4a43c39bd1ca487cc89d7c";
        HttpUtil.sendOkHttp(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean success = false;
                try {
                    success = Utility.handleWeatherResponse(MainActivity.this, new JSONObject(response), myCityId);
                } catch (JSONException e) {
                    e.printStackTrace();
                    new RuntimeException("JSONException");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!success) {
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
                mAnUmbrellaDB.updateWeather(myCityId, "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "更新失败，请检查网络后重试。", Toast.LENGTH_SHORT).show();
                        //showOldWeather();
                    }
                });
            }
        });
    }

//    private void showWeather() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
//        Gson gson = new Gson();
//        mWeather = gson.fromJson(mMyCityList.get(sSelectedCity).getWeather(), Weather.class);
//        if (mWeather == null) {
//            queryWeatherInfo(mCityCode);
//        } else {
//            mTempText.setText(mWeather.daily_forecast.get(0).tmp.min + "°C ~ " + mWeather.daily_forecast.get(0).tmp
// .max
//                    + "°C");
//            mWeatherDespText.setText(mWeather.now.cond.txt + " " + mWeather.now.tmp + "°C");
//            String publishTime = mWeather.basic.update.loc;
//            mPublishText.setText(publishTime.substring(publishTime.length() - 11) + " 发布");
//            mImgWeather.setImageResource(WeatherCode.getWeatherCode(Integer.parseInt(mWeather.now.cond.code)));
//            mCurrentDateText.setText(simpleDateFormat.format(new Date()));
//            mWeatherInfoLayout.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "已更新天气 ^__^", Toast.LENGTH_SHORT).show();
//        }
//        initDrawerLayout();
//        Intent service = new Intent(this, AutoUpdateService.class);
//        //startService(service);
//    }

//    private void showOldWeather() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日");
//        Gson gson = new Gson();
//        mWeather = gson.fromJson(mMyCityList.get(sSelectedCity).getWeather(), Weather.class);
//        if (mWeather != null) {
//            mTempText.setText(mWeather.daily_forecast.get(0).tmp.min + "°C ~ " + mWeather.daily_forecast.get(0).tmp
// .max
//                    + "°C");
//            mWeatherDespText.setText(mWeather.now.cond.txt + " " + mWeather.now.tmp + "°C");
//            String publishTime = mWeather.basic.update.loc;
//            mPublishText.setText(publishTime.substring(publishTime.length() - 11) + " 发布");
//            mImgWeather.setImageResource(WeatherCode.getWeatherCode(Integer.parseInt(mWeather.now.cond.code)));
//            mCurrentDateText.setText(simpleDateFormat.format(new Date()));
//            mWeatherInfoLayout.setVisibility(View.VISIBLE);
//        } else {
//            mTempText.setText("无法连接到网络");
//        }
//        initDrawerLayout();
//    }


    private void cityInfoToSql() {
        showProgressDialog();
        HttpUtil.getCitiesFromFile(MainActivity.this, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handleCitiesResponse(mAnUmbrellaDB, response);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            initAMap();
                            initViewPager();
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
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("第一次需要加载城市信息\n请稍等...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void updateWeather(int myCityId, String cityCode) {
        queryWeatherInfo(cityCode, myCityId);
    }

    public void initDrawerLayout(String cityName) {
        mToolbar.setTitle(cityName);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }
}

