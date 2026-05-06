package com.http_s.rest.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import org.json.JSONArray
import org.json.JSONObject

class SqliteCore(context: Context?) {
    private val databaseName = "httprest.db"
    private val databaseVersion = 2
    private val settingsTable = "settings"
    private val headerVariablesTable = "headers_variables"
    private val db: SQLiteDatabase
    private val insertStmtSettings: SQLiteStatement
    private val deleteStmtSettings: SQLiteStatement
    private val insertStmtHeadersVariables: SQLiteStatement
    private val deleteStmtHeadersVariables: SQLiteStatement

    init {
        val openHelper = OpenHelper(context)
        db = openHelper.writableDatabase
        insertStmtSettings = db.compileStatement("insert into $settingsTable(setting_name,setting_value) values (?,?)")
        deleteStmtSettings = db.compileStatement("delete from $settingsTable where setting_name = ?")
        insertStmtHeadersVariables = db.compileStatement("insert into $headerVariablesTable(field_type, field_name, field_value) values (?,?,?)")
        deleteStmtHeadersVariables = db.compileStatement("delete from $headerVariablesTable where field_type = ? and field_name = ?")
    }

    fun closeDb() {
        db.close()
    }

    fun getSetting(inputSetting: String): String {
        return db.query(
            settingsTable,
            arrayOf("setting_value"),
            "setting_name = ?",
            arrayOf(inputSetting),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0).trim() else ""
        }
    }

    fun createSetting(settingName: String?, settingValue: String?) {
        deleteSetting(settingName)
        insertStmtSettings.bindString(1, settingName.orEmpty())
        insertStmtSettings.bindString(2, settingValue.orEmpty())
        insertStmtSettings.executeInsert()
    }

    private fun deleteSetting(settingName: String?) {
        deleteStmtSettings.bindString(1, settingName.orEmpty())
        deleteStmtSettings.executeUpdateDelete()
    }

    fun getHeaderVariable(inputType: String, inputName: String?): JSONArray {
        val output = JSONArray()
        val selection = if (inputName != null) "field_type = ? and field_name = ?" else "field_type = ?"
        val selectionArgs = if (inputName != null) arrayOf(inputType, inputName) else arrayOf(inputType)
        db.query(
            headerVariablesTable,
            arrayOf("field_name", "field_value"),
            selection,
            selectionArgs,
            null,
            null,
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                output.put(
                    JSONObject()
                        .put("name", cursor.getString(0).trim())
                        .put("value", cursor.getString(1).trim())
                )
            }
        }
        return output
    }

    fun createHeaderVariable(fieldType: String?, fieldName: String?, fieldValue: String?) {
        deleteHeaderVariable(fieldType, fieldName)
        insertStmtHeadersVariables.bindString(1, fieldType.orEmpty())
        insertStmtHeadersVariables.bindString(2, fieldName.orEmpty())
        insertStmtHeadersVariables.bindString(3, fieldValue.orEmpty())
        insertStmtHeadersVariables.executeInsert()
    }

    fun deleteHeaderVariable(fieldType: String?, fieldName: String?) {
        deleteStmtHeadersVariables.bindString(1, fieldType.orEmpty())
        deleteStmtHeadersVariables.bindString(2, fieldName.orEmpty())
        deleteStmtHeadersVariables.executeUpdateDelete()
    }

    private inner class OpenHelper(context: Context?) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS $settingsTable (setting_name TEXT, setting_value TEXT)")
            db.execSQL("CREATE TABLE IF NOT EXISTS $headerVariablesTable (field_type TEXT, field_name TEXT, field_value TEXT)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            onCreate(db)
        }
    }
}
