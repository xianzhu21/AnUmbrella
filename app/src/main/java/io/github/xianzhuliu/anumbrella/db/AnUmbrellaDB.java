package io.github.xianzhuliu.anumbrella.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.xianzhuliu.anumbrella.model.City;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class AnUmbrellaDB {
    public static final String DB_NAME = "an_umbrella";

    public static final int VERSION = 1;

    public static boolean update = false;

    private static AnUmbrellaDB anUmbrellaDB;

    private SQLiteDatabase db;

    private AnUmbrellaDB(Context context) {
        AnUmbrellaOpenHelper dbHelper = new AnUmbrellaOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static AnUmbrellaDB getInstance(Context context) {
        if (anUmbrellaDB == null) {
            anUmbrellaDB = new AnUmbrellaDB(context);
        }
        return anUmbrellaDB;
    }


    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", city.getProvinceName());
            values.put("city_name", city.getCityName());
            values.put("county_name", city.getCountyName());
            values.put("city_id", city.getCityId());
            db.insert("City", null, values);
        }
    }

    public List<City> loadCities() {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
