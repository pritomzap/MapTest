package com.example.user.map_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 8/1/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "Student.db";
    public static final String tableName = "personal_table";
    public static final String col1 = "Trip_ID";
    public static final String col2 = "Source_Lat";
    public static final String col3 = "Source_Long";
    public static final String col4 = "Destination_Lat";
    public static final String col5 = "Destination_Long";
    public static final String col6 = "Duration";
    public static final String col7 = "Distence";

    public DataBaseHelper(Context context) {

        super(context, databaseName, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + tableName + " (Trip_ID INTEGER PRIMARY KEY AUTOINCREMENT,Source_Lat TEXT,Source_Long TEXT,Destination_Lat TEXT,Destination_Long ,Duration TEXT ,Distence TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        onCreate(db);
    }
    public boolean insertData(String Source_Lat,String Source_Long,String Destination_Lat,String Destination_Long,String Duration,String Distence){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2,Source_Lat);
        contentValues.put(col3,Source_Long);
        contentValues.put(col4,Destination_Lat);
        contentValues.put(col5,Destination_Long);
        contentValues.put(col6,Duration);
        contentValues.put(col7,Distence);
        long result = db.insert(tableName,null,contentValues);
        if(result == -1)
            return false;
        else
            return true;

    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tableName,null);
        return res;
    }
}
