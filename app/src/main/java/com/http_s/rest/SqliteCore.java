package com.http_s.rest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import org.json.JSONArray;
import org.json.JSONObject;

public class SqliteCore {

    private final String DATABASE_NAME = "httprest.db";
    private final int DATABASE_VERSION = 2;
    private final String SETTINGS_TABLE = "settings";
    private final String HEADERS_VARIABLES_TABLE = "headers_variables";

    private final SQLiteDatabase db;

    private final SQLiteStatement insertStmtSettings;
    private final SQLiteStatement deleteStmtSettings;

    private final SQLiteStatement insertStmtHeadersVariables;
    private final SQLiteStatement deleteStmtHeadersVariables;

    public SqliteCore(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        this.db = openHelper.getWritableDatabase();
        String INSERT_SETTING = "insert into " + SETTINGS_TABLE + "(setting_name,setting_value) values (?,?)";
        this.insertStmtSettings = this.db.compileStatement(INSERT_SETTING);
        String DELETE_SETTING = "delete from " + SETTINGS_TABLE + " where setting_name = ?";
        this.deleteStmtSettings = this.db.compileStatement(DELETE_SETTING);
        String INSERT_HEADER_VARIABLE = "insert into " + HEADERS_VARIABLES_TABLE + "(field_type, field_name, field_value) values (?,?,?)";
        this.insertStmtHeadersVariables = this.db.compileStatement(INSERT_HEADER_VARIABLE);
        String DELETE_HEADER_VARIABLE = "delete from " + HEADERS_VARIABLES_TABLE + " where field_type = ? and field_name = ?";
        this.deleteStmtHeadersVariables = this.db.compileStatement(DELETE_HEADER_VARIABLE);
    }

    public void closeDb() {
        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //--------------------   SETTINGS   --------------------
    public String getSetting(String inputSetting) {
        String output = "";

        try {
            Cursor cursor = db.query(SETTINGS_TABLE, new String[]{"setting_value"}, "setting_name = ?", new String[]{inputSetting}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    output = cursor.getString(0).trim();
                } while (cursor.moveToNext());
            }

            if (cursor.isClosed()) {
                cursor.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    public void createSetting(String settingName, String settingValue) {
        deleteSetting(settingName);
        this.insertStmtSettings.bindString(1, settingName);
        this.insertStmtSettings.bindString(2, settingValue);
        this.insertStmtSettings.executeInsert();
    }

    public void deleteSetting(String settingName) {
        this.deleteStmtSettings.bindString(1, settingName);
        this.deleteStmtSettings.executeInsert();
    }

    //--------------------   HEADERS / VARIABLES   --------------------
    public JSONArray getHeaderVariable(String inputType, String inputName) {
        JSONArray output = new JSONArray();

        try {
            Cursor cursor;
            if (inputName != null) {
                cursor = db.query(HEADERS_VARIABLES_TABLE, new String[]{"field_name, field_value"}, "field_type = ? and field_name = ?", new String[]{inputType, inputName}, null, null, null);
            } else {
                cursor = db.query(HEADERS_VARIABLES_TABLE, new String[]{"field_name, field_value"}, "field_type = ?", new String[]{inputType}, null, null, null);
            }
            if (cursor.moveToFirst()) {
                do {
                    JSONObject record = new JSONObject();
                    record.put("name", cursor.getString(0).trim());
                    record.put("value", cursor.getString(1).trim());
                    output.put(record);
                } while (cursor.moveToNext());
            }

            if (cursor.isClosed()) {
                cursor.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    public void createHeaderVariable(String fieldType, String fieldName, String fieldValue) {
        deleteHeaderVariable(fieldType, fieldName);
        this.insertStmtHeadersVariables.bindString(1, fieldType);
        this.insertStmtHeadersVariables.bindString(2, fieldName);
        this.insertStmtHeadersVariables.bindString(3, fieldValue);
        this.insertStmtHeadersVariables.executeInsert();
    }

    public void deleteHeaderVariable(String fieldType, String fieldName) {
        this.deleteStmtHeadersVariables.bindString(1, fieldType);
        this.deleteStmtHeadersVariables.bindString(2, fieldName);
        this.deleteStmtHeadersVariables.executeInsert();
    }


    private class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //TRY TO CREATE THE DATABASE TABLES, BUT IT WILL ONLY SUCCEED IF THEY DON'T EXIST
            db.execSQL("CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE + " (setting_name TEXT, setting_value TEXT)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + HEADERS_VARIABLES_TABLE + " (field_type TEXT, field_name TEXT, field_value TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }
}

