package com.http_s.rest.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import org.json.JSONArray
import org.json.JSONObject

class SqliteCore(context: Context?) {
    private val _databaseName = "httprest.db"
    private val _databaseVersion = 2
    private val _settingsTable = "settings"
    private val headerVariablesTable = "headers_variables"

    private val db: SQLiteDatabase

    private val insertStmtSettings: SQLiteStatement
    private val deleteStmtSettings: SQLiteStatement

    private val insertStmtHeadersVariables: SQLiteStatement
    private val deleteStmtHeadersVariables: SQLiteStatement

    init {
        val openHelper = OpenHelper(context)
        this.db = openHelper.writableDatabase
        val insertSetting =
            "insert into $_settingsTable(setting_name,setting_value) values (?,?)"
        this.insertStmtSettings = db.compileStatement(insertSetting)
        val deleteSetting = "delete from $_settingsTable where setting_name = ?"
        this.deleteStmtSettings = db.compileStatement(deleteSetting)
        val insertHeaderVariable =
            "insert into $headerVariablesTable(field_type, field_name, field_value) values (?,?,?)"
        this.insertStmtHeadersVariables = db.compileStatement(insertHeaderVariable)
        val deleteHeaderVariable =
            "delete from $headerVariablesTable where field_type = ? and field_name = ?"
        this.deleteStmtHeadersVariables = db.compileStatement(deleteHeaderVariable)
    }

    fun closeDb() {
        try {
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //--------------------   SETTINGS   --------------------
    fun getSetting(inputSetting: String): String {
        var output = ""

        try {
            val cursor = db.query(
                _settingsTable,
                arrayOf("setting_value"),
                "setting_name = ?",
                arrayOf(inputSetting),
                null,
                null,
                null
            )
            if (cursor.moveToFirst()) {
                do {
                    output = cursor.getString(0).trim { it <= ' ' }
                } while (cursor.moveToNext())
            }

            if (cursor.isClosed) {
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output
    }

    fun createSetting(settingName: String?, settingValue: String?) {
        deleteSetting(settingName)
        insertStmtSettings.bindString(1, settingName)
        insertStmtSettings.bindString(2, settingValue)
        insertStmtSettings.executeInsert()
    }

    private fun deleteSetting(settingName: String?) {
        deleteStmtSettings.bindString(1, settingName)
        deleteStmtSettings.executeInsert()
    }

    //--------------------   HEADERS / VARIABLES   --------------------
    fun getHeaderVariable(inputType: String, inputName: String?): JSONArray {
        val output = JSONArray()

        try {
            val cursor = if (inputName != null) {
                db.query(
                    headerVariablesTable,
                    arrayOf("field_name, field_value"),
                    "field_type = ? and field_name = ?",
                    arrayOf(inputType, inputName),
                    null,
                    null,
                    null
                )
            } else {
                db.query(
                    headerVariablesTable,
                    arrayOf("field_name, field_value"),
                    "field_type = ?",
                    arrayOf(inputType),
                    null,
                    null,
                    null
                )
            }
            if (cursor.moveToFirst()) {
                do {
                    val record = JSONObject()
                    record.put("name", cursor.getString(0).trim { it <= ' ' })
                    record.put("value", cursor.getString(1).trim { it <= ' ' })
                    output.put(record)
                } while (cursor.moveToNext())
            }

            if (cursor.isClosed) {
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output
    }

    fun createHeaderVariable(fieldType: String?, fieldName: String?, fieldValue: String?) {
        deleteHeaderVariable(fieldType, fieldName)
        insertStmtHeadersVariables.bindString(1, fieldType)
        insertStmtHeadersVariables.bindString(2, fieldName)
        insertStmtHeadersVariables.bindString(3, fieldValue)
        insertStmtHeadersVariables.executeInsert()
    }

    fun deleteHeaderVariable(fieldType: String?, fieldName: String?) {
        deleteStmtHeadersVariables.bindString(1, fieldType)
        deleteStmtHeadersVariables.bindString(2, fieldName)
        deleteStmtHeadersVariables.executeInsert()
    }


    private inner class OpenHelper(context: Context?) :
        SQLiteOpenHelper(context, _databaseName, null, _databaseVersion) {
        override fun onOpen(db: SQLiteDatabase) {
        }

        override fun onCreate(db: SQLiteDatabase) {
            //TRY TO CREATE THE DATABASE TABLES, BUT IT WILL ONLY SUCCEED IF THEY DON'T EXIST
            db.execSQL("CREATE TABLE IF NOT EXISTS $_settingsTable (setting_name TEXT, setting_value TEXT)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $headerVariablesTable (field_type TEXT, field_name TEXT, field_value TEXT)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onCreate(db)
        }
    }
}

