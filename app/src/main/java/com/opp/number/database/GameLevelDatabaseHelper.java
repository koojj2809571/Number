package com.opp.number.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.opp.number.database.DatabaseConstant.GameLevelTable;
import com.opp.number.database.DatabaseConstant.GameLevelTable.Cols;

public class GameLevelDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE_SQL = "CREATE TABLE "
                + GameLevelTable.TABLE_NAME
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Cols.ORDER_NUMBER + " INTEGER, "
                + Cols.NAME + " TEXT, "
                + Cols.INIT_SETTING + " TEXT)";

    public GameLevelDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public GameLevelDatabaseHelper(Context context){
        super(context, "gameLevel.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
