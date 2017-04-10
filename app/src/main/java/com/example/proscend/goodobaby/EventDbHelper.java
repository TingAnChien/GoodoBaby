package com.example.proscend.goodobaby;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by proscend on 2015/7/28.
 */
public class EventDbHelper  extends SQLiteOpenHelper{


    public  String sCreateTableCommand;
    public  EventDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
        sCreateTableCommand = "";
    }


    @Override
    public void onCreate (SQLiteDatabase db){
        if(sCreateTableCommand.isEmpty())
            return;

        db.execSQL(sCreateTableCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldver, int newVer){


    }
}