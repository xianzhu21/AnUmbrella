package io.github.xianzhuliu.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LiuXianzhu on 18/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

//    public static final String CREATE_PROVINCE = "create table Province ("
//            + "id integer primary key autoincrement, "
//            + "province_name text, "
//            + "province_code text)";

    public static final String CREATE_LOCATION = "create table Location ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "city_name text, "
            + "county_name text, "
            + "location_code text)";

    public static final String DELETE_PROVINCE = "drop table Province";
    public static final String DELETE_CITY = "drop table City";
    public static final String DELETE_COUNTY = "drop table County";

//    public static final String CREATE_COUNTY = "create table County ("
//            + "id integer primary key autoincrement, "
//            + "county_name text, "
//            + "county_code text, "
//            + "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_LOCATION);
        //db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(DELETE_PROVINCE);
                db.execSQL(DELETE_CITY);
                db.execSQL(DELETE_COUNTY);
                db.execSQL(CREATE_LOCATION);
        }
    }
}
