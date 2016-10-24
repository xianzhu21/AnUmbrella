package io.github.xianzhuliu.anumbrella.db;

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
            + "city_id text)";

    public AnUmbrellaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
