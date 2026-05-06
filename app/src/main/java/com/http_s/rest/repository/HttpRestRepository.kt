package com.http_s.rest.repository

import android.content.Context
import android.util.Base64
import com.http_s.rest.Utilities
import com.http_s.rest.data.SqliteCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class HttpRestRepository(context: Context) {
    private val appContext = context.applicationContext
    private val utilities = Utilities(appContext)
    private val sqliteCore = SqliteCore(appContext)

    fun getSetting(name: String): String = sqliteCore.getSetting(name)

    fun saveSetting(name: String, value: String) {
        sqliteCore.createSetting(name, value)
    }

    fun getFields(type: String): List<Pair<String, String>> {
        val records = sqliteCore.getHeaderVariable(type, null)
        return buildList {
            for (i in 0 until records.length()) {
                val item = records.getJSONObject(i)
                add(item.optString("name") to item.optString("value"))
            }
        }
    }

    fun replaceFields(type: String, values: List<Pair<String, String>>) {
        val previous = sqliteCore.getHeaderVariable(type, null)
        for (i in 0 until previous.length()) {
            sqliteCore.deleteHeaderVariable(type, previous.getJSONObject(i).optString("name"))
        }
        values
            .filter { it.first.isNotBlank() }
            .forEach { sqliteCore.createHeaderVariable(type, it.first.trim(), it.second.trim()) }
    }

    fun readAssetText(name: String): String = utilities.getFileContentFromAssets(name).orEmpty()

    suspend fun sendRequest(
        method: String,
        url: String,
        headers: List<Pair<String, String>>,
        variables: List<Pair<String, String>>
    ): JSONObject = withContext(Dispatchers.IO) {
        val normalizedMethod = method.uppercase()
        val query = buildVariableQuery(normalizedMethod, variables)
        val finalUrl = if (normalizedMethod == "GET" && query.isNotBlank()) "$url?$query" else url
        sqliteCore.createSetting(Utilities.HTTP_URL, finalUrl)
        sqliteCore.createSetting(Utilities.HTTP_CONNECTION_OPENED, utilities.currentTimeStamp)

        val connection = (URL(finalUrl).openConnection() as HttpURLConnection).apply {
            this.requestMethod = normalizedMethod
            connectTimeout = 20_000
            readTimeout = 30_000
            doInput = true
            useCaches = false
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            setRequestProperty("Content-Language", "en-US")
            headers.filter { it.first.isNotBlank() }.forEach { (name, value) ->
                setRequestProperty(name.trim(), value.trim())
            }
        }

        val headerSummary = headers.filter { it.first.isNotBlank() }.joinToString(separator = "\n\n") {
            "Name: ${it.first.trim()}\nValue: ${it.second.trim()}"
        }
        sqliteCore.createSetting(Utilities.HTTP_OUTGOING_HEADER_SENT, headerSummary)
        sqliteCore.createSetting(Utilities.HTTP_OUTGOING_HEADER_COUNT_SENT, headers.count { it.first.isNotBlank() }.toString())

        val canWriteBody = normalizedMethod in setOf("POST", "PUT", "DELETE")
        val bodyBytes = query.toByteArray(StandardCharsets.UTF_8)
        sqliteCore.createSetting(Utilities.HTTP_OUTGOING_CONTENT_LENGTH, "${bodyBytes.size} ${if (bodyBytes.size == 1) "byte" else "bytes"}")

        if (canWriteBody && query.isNotBlank()) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Length", bodyBytes.size.toString())
            sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_OPEN_STREAM, utilities.currentTimeStamp)
            DataOutputStream(connection.outputStream).use { stream ->
                stream.write(bodyBytes)
                stream.flush()
            }
            sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_STREAM_SENT, utilities.currentTimeStamp)
            sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_CLOSE_STREAM, utilities.currentTimeStamp)
        }

        sqliteCore.createSetting(Utilities.HTTP_INCOMING_BEGIN, utilities.currentTimeStamp)
        val responseCode = connection.responseCode
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_CODE, responseCode.toString())
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_DESCRIPTION, utilities.getHttpResponseTypeDescription(responseCode))
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_BEGIN_READING_CONTENTS, utilities.currentTimeStamp)

        val input = if (responseCode in 200..299) connection.inputStream else connection.errorStream
        val responseText = input?.use { stream ->
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { reader ->
                buildString {
                    while (true) {
                        val line = reader.readLine() ?: break
                        append(line)
                    }
                }
            }
        }.orEmpty()
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_END_READING_CONTENTS, utilities.currentTimeStamp)
        sqliteCore.createSetting(
            Utilities.HTTP_INCOMING_CONTENTS_LENGTH,
            "${responseText.toByteArray(StandardCharsets.UTF_8).size} ${if (responseText.toByteArray(StandardCharsets.UTF_8).size == 1) "byte" else "bytes"}"
        )

        val responseHeaders = JSONObject()
        connection.headerFields.forEach { (key, value) ->
            responseHeaders.put(key ?: ".", value.joinToString(", "))
        }
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_HEADER_COUNT, connection.headerFields.size.toString())

        JSONObject()
            .put("response_code", responseCode)
            .put("response_code_description", utilities.getHttpResponseTypeDescription(responseCode))
            .put("response_code_message", connection.responseMessage.orEmpty())
            .put("response_message", responseText)
            .put("headers", responseHeaders)
    }

    private fun buildVariableQuery(method: String, variables: List<Pair<String, String>>): String {
        val merged = variables.toMutableList()
        if (sqliteCore.getSetting(Utilities.LOCATION_SETTING).equals("YES", ignoreCase = true)) {
            merged.add(Utilities.LATITUDE to sqliteCore.getSetting(Utilities.LATITUDE))
            merged.add(Utilities.LONGITUDE to sqliteCore.getSetting(Utilities.LONGITUDE))
        }

        var query = merged
            .filter { it.first.isNotBlank() }
            .joinToString("&") { (name, rawValue) ->
                val value = when {
                    method == "GET" && sqliteCore.getSetting(Utilities.ENCODE_GET_VALUES_SETTING).equals("YES", true) -> rawValue.base64()
                    method != "GET" && sqliteCore.getSetting(Utilities.ENCODE_POST_VALUES_SETTING).equals("YES", true) -> rawValue.base64()
                    else -> rawValue
                }
                "${name.trim().urlEncode()}=${value.trim().urlEncode()}"
            }

        if (method == "GET" && sqliteCore.getSetting(Utilities.ENCODE_GET_QUERY_STRING).equals("YES", true)) {
            query = query.base64()
        }
        sqliteCore.createSetting(Utilities.HTTP_OUTGOING_VARIABLES_SENT, query)
        return query
    }

    private fun String.base64(): String = Base64.encodeToString(toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    private fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.name())
}
