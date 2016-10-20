package io.github.xianzhuliu.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.xianzhuliu.coolweather.model.Location;

/**
 * Created by LiuXianzhu on 19/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";

    public static final int VERSION = 2;

    public static boolean update = true;

    private static CoolWeatherDB coolWeatherDB;

    private SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context) {
        if (coolWeatherDB == null) {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

//    public void saveProvince(Province province) {
//        if (province != null) {
//            ContentValues values = new ContentValues();
//            values.put("province_name", province.getProvinceName());
//            values.put("province_code", province.getProvinceCode());
//            db.insert("Province", null, values);
//        }
//    }
//
//    public List<Province> loadProvinces() {
//        List<Province> list = new ArrayList<>();
//        Cursor cursor = db.query("Province", null, null, null, null, null, null);
//        if (cursor.moveToFirst()) {
//            do {
//                Province province = new Province();
//                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
//                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
//                list.add(province);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return list;
//    }

    public void saveLocation(Location location) {
        if (location != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", location.getProvinceName());
            values.put("city_name", location.getCityName());
            values.put("county_name", location.getCountyName());
            values.put("location_code", location.getLocationCode());
            db.insert("Location", null, values);
        }
    }

    public List<Location> loadLocations() {
        List<Location> list = new ArrayList<>();
        Cursor cursor = db.query("Location", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setId(cursor.getInt(cursor.getColumnIndex("id")));
                location.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                location.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                location.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                location.setLocationCode(cursor.getString(cursor.getColumnIndex("location_code")));
                list.add(location);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

//    public void saveCounty(County county) {
//        if (county != null) {
//            ContentValues values = new ContentValues();
//            values.put("county_name", county.getCountyName());
//            values.put("county_code", county.getCountyCode());
//            values.put("city_id", county.getCityId());
//            db.insert("County", null, values);
//        }
//    }
//
//    public List<County> loadCounties(int cityId) {
//        List<County> list = new ArrayList<>();
//        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null,
// null);
//        if (cursor.moveToFirst()) {
//            do {
//                County county = new County();
//                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
//                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_code")));
//                county.setCityId(cityId);
//                list.add(county);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return list;
//    }
}
