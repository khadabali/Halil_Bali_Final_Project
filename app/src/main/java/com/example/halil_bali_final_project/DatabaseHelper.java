package com.example.halil_bali_final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "NASA";
    private final static int VERSION_NUM = 1;

    public final static String TABLE_NAME = "IMAGE";
    public final static String COL_ID = "id";
    public final static String COL_DATE = "date";
    public final static String COL_URL = "url";
    public final static String COL_TITLE = "title";

    public DatabaseHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+
                COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_DATE+" TEXT, "+
                COL_URL+" TEXT, "+
                COL_TITLE+" TEXT);");
        Log.d(getClass().getName(),"inside onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Log.d(getClass().getName(),"inside onUpgrade()");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Log.d(getClass().getName(),"inside onDowngrade()");
    }
}

