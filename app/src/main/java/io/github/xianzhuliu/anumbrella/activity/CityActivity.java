package io.github.xianzhuliu.anumbrella.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import io.github.xianzhuliu.anumbrella.util.Utility;
import io.github.xianzhuliu.anumbrella.util.WeatherCode;

public class CityActivity extends AppCompatActivity {

    private static final String TAG = "CityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.city_toolbar);
        toolbar.setTitle("城市管理");
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
        final AnUmbrellaDB anUmbrellaDB = AnUmbrellaDB.getInstance(getApplicationContext());
        List<MyCity> myCityList = anUmbrellaDB.loadMyCities();
        final List<MyCityBean> beanList = new ArrayList<>();
        for (MyCity myCity : myCityList) {
            if (myCity.getCityId() == -1) {
                // 没有定位的城市
                continue;
            }
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

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(R.color.colorRed);
                deleteItem.setWidth(Utility.dp2px(getApplicationContext(), 80));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lv_city);
        final MyCityAdapter myCityAdapter = new MyCityAdapter(this, beanList);
        listView.setAdapter(myCityAdapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        String cityName = beanList.get(position).getMyCityName();
                        City city = anUmbrellaDB.findCityByName(cityName);
                        MyCity myCity = anUmbrellaDB.findMyCityByCityId(city.getId());
                        Log.d(TAG, "onMenuItemClick: id===" + myCity.getId());
                        if (myCity.getId() == 1) {
                            // 存定位城市的记录不删除
                            anUmbrellaDB.updateMyCity(1, -1);
                            Toast.makeText(CityActivity.this, "已删除" + cityName, Toast.LENGTH_SHORT).show();
                            beanList.remove(position);
                            myCityAdapter.notifyDataSetChanged();
                        } else {
                            if (anUmbrellaDB.deleteMyCity(city.getId()) != 0) {
                                Toast.makeText(CityActivity.this, "已删除" + cityName, Toast.LENGTH_SHORT).show();
                                beanList.remove(position);
                                myCityAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(CityActivity.this, "删除" + cityName + "失败，给作者吐槽下吧", Toast
                                        .LENGTH_SHORT).show();
                            }
                        }
                        MainActivity.sUpdateMyCity = true;
                        break;
                }
                return false;
            }
        });
    }
}
