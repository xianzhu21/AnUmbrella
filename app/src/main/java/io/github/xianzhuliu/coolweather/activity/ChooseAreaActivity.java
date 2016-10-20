package io.github.xianzhuliu.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.xianzhuliu.coolweather.R;
import io.github.xianzhuliu.coolweather.db.CoolWeatherDB;
import io.github.xianzhuliu.coolweather.model.Location;
import io.github.xianzhuliu.coolweather.util.HttpCallbackListener;
import io.github.xianzhuliu.coolweather.util.HttpUtil;
import io.github.xianzhuliu.coolweather.util.Utility;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class ChooseAreaActivity extends Activity {
    private static final String TAG = "ChooseAreaActivity";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<>();
    private List<Location> locationList;
    private String selectedProvince;
    private String selectedCity;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = dataList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = dataList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();
    }

    private void queryCounties() {
        if (locationList == null || locationList.isEmpty()) {
            locationList = coolWeatherDB.loadLocations();
        }
        if (locationList.size() > 0) {
            dataList.clear();
            Set<String> set = new HashSet<>();
            for (Location location : locationList) {
                if (location.getCityName().equals(selectedCity)) {
                    set.add(location.getCountyName());
                }
            }
            dataList.addAll(set);
            Collator collatorChinese = Collator.getInstance(Locale.CHINESE);
            Collections.sort(dataList, collatorChinese);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer("county");
        }
    }


    private void queryCities() {
        if (locationList == null || locationList.isEmpty()) {
            locationList = coolWeatherDB.loadLocations();
        }
        if (locationList.size() > 0) {
            dataList.clear();
            Set<String> set = new HashSet<>();
            for (Location location : locationList) {
                if (location.getProvinceName().equals(selectedProvince)) {
                    set.add(location.getCityName());
                }
            }
            dataList.addAll(set);
            Collator collatorChinese = Collator.getInstance(Locale.CHINESE);
            Collections.sort(dataList, collatorChinese);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince);
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer("city");
        }
    }

    private void queryProvinces() {
        if (locationList == null || locationList.isEmpty()) {
            locationList = coolWeatherDB.loadLocations();
        }
        if (locationList.size() > 0 && !CoolWeatherDB.update) {
            dataList.clear();
            Set<String> set = new HashSet<>();
            for (Location location : locationList) {
                set.add(location.getProvinceName());
            }
            dataList.addAll(set);
            for (String s : dataList) {
            }
            Collator collatorChinese = Collator.getInstance(Locale.CHINESE);
            Collections.sort(dataList, collatorChinese);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("province");
            CoolWeatherDB.update = false;
        }
    }

    private void queryFromServer(final String type) {
        showProgressDialog();
        HttpUtil.getLocationsFromFile(ChooseAreaActivity.this, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handleLocationsResponse(coolWeatherDB, response);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
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
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载城市信息...");
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
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
