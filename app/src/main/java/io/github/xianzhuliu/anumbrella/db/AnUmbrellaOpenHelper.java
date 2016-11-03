package io.github.xianzhuliu.anumbrella.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LiuXianzhu on 18/10/2016.
 * Contact: liuxianzhu0221@gmail.com
 */

public class AnUmbrellaOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "province_name text, "
            + "city_name text, "
            + "county_name text, "
            + "city_code text)";
    public static final String CREATE_MY_CITY = "create table MyCity ("
            + "id integer primary key autoincrement, "
            + "city_id integer, "
            + "weather text)";

    public AnUmbrellaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_MY_CITY);
        ContentValues values = new ContentValues();
        values.put("city_id", -1); // id为1的位置存储自动定位的城市，city_id为-1说明没有定位的城市
        db.insert("MyCity", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
