package io.github.xianzhuliu.anumbrella.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.xianzhuliu.anumbrella.R;
import io.github.xianzhuliu.anumbrella.adapter.MyCityAdapter;
import io.github.xianzhuliu.anumbrella.db.AnUmbrellaDB;
import io.github.xianzhuliu.anumbrella.model.City;
import io.github.xianzhuliu.anumbrella.model.MyCity;
import io.github.xianzhuliu.anumbrella.model.MyCityBean;
import io.github.xianzhuliu.anumbrella.model.Weather;
import io.github.xianzhuliu.anumbrella.util.WeatherCode;

public class CityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.city_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starter = new Intent(CityActivity.this, ChooseAreaActivity.class);
                startActivity(starter);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnUmbrellaDB anUmbrellaDB = AnUmbrellaDB.getInstance(this);
        List<MyCity> myCityList = anUmbrellaDB.loadMyCities();
        List<MyCityBean> beanList = new ArrayList<>();
        for (MyCity myCity : myCityList) {
            City city = anUmbrellaDB.findCityById(myCity.getCityId());
            String tmp = ""; // 气温
            int cond = -1; // 天气情况图
            String weatherInfo = myCity.getWeather();
            if (!TextUtils.isEmpty(weatherInfo)) {
                Gson gson = new Gson();
                Weather weather = gson.fromJson(weatherInfo, Weather.class);
                tmp = weather.now.tmp;
                cond = WeatherCode.getWeatherCode(Integer.parseInt(weather.now.cond.code));
            }
            beanList.add(new MyCityBean(city.getCountyName(), tmp + "°C", cond));
        }

        ListView listView = (ListView) findViewById(R.id.lv_city);
        listView.setAdapter(new MyCityAdapter(this, beanList));
    }
}
