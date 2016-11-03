package io.github.xianzhuliu.anumbrella.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.xianzhuliu.anumbrella.model.City;
import io.github.xianzhuliu.anumbrella.model.MyCity;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class AnUmbrellaDB {
    private static final String TAG = "AnUmbrellaDB";
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

    private City createCityFromCursor(Cursor cursor) {
        City city = new City();
        city.setId(cursor.getInt(cursor.getColumnIndex("id")));
        city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
        city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
        city.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
        city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
        return city;
    }
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", city.getProvinceName());
            values.put("city_name", city.getCityName());
            values.put("county_name", city.getCountyName());
            values.put("city_code", city.getCityCode());
            db.insert("City", null, values);
        } else {
            throw new NullPointerException("city is null");
        }
    }

    public List<City> loadCities() {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = createCityFromCursor(cursor);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public City findCityById(int id) {
        Cursor cursor = db.query("City", null, "id = ? ", new String[]{"" + id}, null, null, null);
        City city;
        if (cursor.moveToFirst()) {
            city = createCityFromCursor(cursor);
        } else {
            throw new RuntimeException("query error");
        }
        cursor.close();
        return city;
    }

    public City findCityFromLocation(String cityName, String district) {
        Cursor cursor = db.query("City", null, "city_name = ? AND county_name LIKE ?", new String[]{cityName,
                "%" + district + "%"}, null, null, null);
        City city = null;
        if (cursor.moveToFirst()) {
            city = createCityFromCursor(cursor);
        } else {
            cursor = db.query("City", null, "city_name = ? AND county_name LIKE ?", new String[]{cityName, cityName},
                    null, null, null);
            if (cursor.moveToFirst()) {
                city = createCityFromCursor(cursor);
            }
        }
        return city;
    }

    public City findCityByName(String countyName) {
        Cursor cursor = db.query("City", null, "county_name = ? ", new String[]{"" +
                        countyName}, null, null,
                null);
        City city = new City();
        if (cursor.moveToFirst()) {
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
        } else {
            throw new RuntimeException("query error");
        }
        cursor.close();
        return city;
    }

    public long saveMyCity(City myCity) {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        if (myCity != null) {
            values.put("city_id", myCity.getId());
            db.setTransactionSuccessful();
        } else {
            throw new NullPointerException("myCity is null");
        }
        db.endTransaction();
        return db.insert("MyCity", null, values);
    }

    public MyCity findMyCityById(int id) {
        Cursor cursor = db.query("MyCity", null, "id = ? ", new String[]{"" + id}, null, null, null);
        MyCity myCity = new MyCity();
        if (cursor.moveToFirst()) {
            myCity.setId(id);
            myCity.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
            myCity.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
        } else {
            throw new RuntimeException("query error");
        }
        return myCity;
    }

    public MyCity findMyCityByCityId(int cityId) {
        Cursor cursor = db.query("MyCity", null, "city_id = ? ", new String[]{"" +
                        cityId}, null, null,
                null);
        MyCity myCity = new MyCity();
        if (cursor.moveToFirst()) {
            myCity.setId(cursor.getInt(cursor.getColumnIndex("id")));
            myCity.setCityId(cityId);
            myCity.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
        } else {
            throw new RuntimeException("query error");
        }
        return myCity;
    }

    public int deleteMyCity(int cityId) {
        db.beginTransaction();
        int res = db.delete("MyCity", "city_id = ?", new String[]{"" + cityId});
        db.setTransactionSuccessful();
        db.endTransaction();
        return res;
    }

    public void updateWeather(int id, String weather) {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("weather", weather);
        db.update("MyCity", values, "id = ?", new String[]{"" + id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateMyCity(int id, int cityId) {
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("city_id", cityId);
        db.update("MyCity", values, "id = ?", new String[]{"" + id});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<MyCity> loadMyCities() {
        List<MyCity> list = new ArrayList<>();
        Cursor cursor = db.query("MyCity", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MyCity myCity = new MyCity();
                myCity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                myCity.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                myCity.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                list.add(myCity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
