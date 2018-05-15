package com.opp.number.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.opp.number.bean.ConnectionSetting;
import com.opp.number.bean.LevelSetting;
import com.opp.number.database.DatabaseConstant.GameLevelTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private static final String TAG = "数据库工具类";

    public static List<LevelSetting> getInitDataFromDatabase(SQLiteDatabase database) throws JSONException {
        List<LevelSetting> settings = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM game_level ORDER BY "
                + GameLevelTable.Cols.ORDER_NUMBER, null);
        if (cursor.moveToFirst()) {
            do {
                LevelSetting setting = new LevelSetting();
                setting.setOrder(cursor.getInt(cursor.getColumnIndex(GameLevelTable.Cols.ORDER_NUMBER)));
                setting.setName(cursor.getString(cursor.getColumnIndex(GameLevelTable.Cols.NAME)));
                String s = cursor.getString(cursor.getColumnIndex(GameLevelTable.Cols.INIT_SETTING));
                setting.setInitSetting(stringToList(s));
                settings.add(setting);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return settings;
    }

    public static void insertData(LevelSetting setting, SQLiteDatabase database) throws JSONException {

        if (!isItemExist(database,setting.getName())) {
            String jsonString = listToString(setting);
            database.execSQL("INSERT INTO " + GameLevelTable.TABLE_NAME
                            + " (" + GameLevelTable.Cols.ORDER_NUMBER + ", "
                            + GameLevelTable.Cols.NAME + ", "
                            + GameLevelTable.Cols.INIT_SETTING + ")"
                            + " VALUES(?, ?, ?)"
                    , new String[]{setting.getOrder() + "", setting.getName(), jsonString});
        }
    }

    public static ConnectionSetting[] queryItem(SQLiteDatabase database, int order) throws JSONException {
        String queryString = "SELECT * FROM "
                + GameLevelTable.TABLE_NAME + " WHERE " + GameLevelTable.Cols.ORDER_NUMBER + " = ?";
        Cursor cursor = database.rawQuery(queryString,new String[]{order + ""});
        cursor.moveToFirst();
        String jsonString = cursor.getString(cursor.getColumnIndex(GameLevelTable.Cols.INIT_SETTING));
        List<ConnectionSetting> list = stringToList(jsonString);
        ConnectionSetting[] connectionSettings = list.toArray(new ConnectionSetting[list.size()]);
        cursor.close();
        return connectionSettings;
    }

    private static boolean isItemExist(SQLiteDatabase database,String name) {
        String queryString = "SELECT " + GameLevelTable.Cols.NAME + " FROM "
                + GameLevelTable.TABLE_NAME + " WHERE " + GameLevelTable.Cols.NAME + " = ?";
        Cursor cursor = database.rawQuery(queryString,new String[]{name});
        boolean b = cursor.moveToNext();
        cursor.close();
        return b;
    }

    private static String listToString(LevelSetting setting) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < setting.getInitSetting().size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("isPort", setting.getInitSetting().get(i).isPort());
            jsonObject.put("portNumber", setting.getInitSetting().get(i).getPortNumber());
            jsonObject.put("lineColor", setting.getInitSetting().get(i).getLineColor());
            jsonArray.put(i, jsonObject);
        }

        return jsonArray.toString();
    }

    private static List<ConnectionSetting> stringToList(String s) throws JSONException {

        List<ConnectionSetting> list = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(s);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ConnectionSetting setting1 = new ConnectionSetting();
            setting1.setPort(jsonObject.getBoolean("isPort"));
            setting1.setPortNumber(jsonObject.getInt("portNumber"));
            setting1.setLineColor(jsonObject.getInt("lineColor"));
            list.add(setting1);
        }

        return list;
    }
}
