package com.example.lenovo.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class myDB extends SQLiteOpenHelper {
    private String table1="create table passWord(_id integer primary key autoincrement,"+
            "type text,"+"number text,"+"password text)";//账号以及密码
    private String table2="create table alarm(alarm_name text primary key,"+"hour integer,"
            +"minute integer,"+"state text,"+"repeatTimes text,"+"problemNum text)";//闹钟
    private String table3="create table memory(time text primary key,event text,details text)";//备忘录
    public myDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table1);
        db.execSQL(table2);
        db.execSQL(table3);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists passWord");
        db.execSQL("drop table if exists alarm");
        onCreate(db);
    }
}
