package com.http_s.rest.api_client

import android.app.Activity
import android.util.Base64
import android.util.Log
import com.http_s.rest.Utilities
import com.http_s.rest.data.SqliteCore
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

abstract class HttpClient(
    inputActivity: Activity?,
    private val requestMethod: String,
    private val webServiceURL: String,
    private val headerData: JSONArray,
    private val variableData: JSONArray
) {
    private val utilities = inputActivity?.let { Utilities(it) }
    private val sqliteCore = SqliteCore(inputActivity)
    private var responseData: JSONObject? = null
    private var httpResponse = ""
    private var resultHTTPClient = "RUNNING"
    private var httpURLConnection: HttpURLConnection? = null
    private var future: Future<*>? = null
    private lateinit var executorService: ExecutorService

    inner class HttpTask : Callable<Any?> {
        override fun call(): String {
            try {
                runHttpProcess()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return resultHTTPClient
        }

        private fun runHttpProcess() {
            Log.d("HttpClient", "runHttpProcess")
            val `object`: URL
            try {
                val queryString: String = variablesQuery

                val finalUrl = if (requestMethod.equals("GET", ignoreCase = true)) {
                    "$webServiceURL?$queryString"
                } else {
                    webServiceURL
                }

                `object` = URL(finalUrl)
                sqliteCore.createSetting(Utilities.HTTP_URL, finalUrl)

                sqliteCore.createSetting(
                    Utilities.HTTP_CONNECTION_OPENED,
                    utilities?.currentTimeStamp
                )
                httpURLConnection = `object`.openConnection() as HttpURLConnection
                httpURLConnection!!.doOutput = true
                httpURLConnection!!.doInput = true
                httpURLConnection!!.useCaches = false

                val headerCount = headerData.length()
                val headerSummary = StringBuilder()
                Log.d("HttpClient", "runHttpProcess -> headerCount = $headerCount")
                if (headerCount > 0) {
                    for (i in 0 until headerCount) {
                        val record = headerData.getJSONObject(i)
                        httpURLConnection!!.setRequestProperty(
                            record.getString("name").trim { it <= ' ' },
                            record.getString("value").trim { it <= ' ' })
                        headerSummary.append(
                            """
                                Name: ${record.getString("name").trim { it <= ' ' }}
                                Value: ${record.getString("value").trim { it <= ' ' }}
                                
                                """.trimIndent()
                        )
                    }
                    sqliteCore.createSetting(
                        Utilities.HTTP_OUTGOING_HEADER_SENT,
                        headerSummary.toString()
                    )
                    sqliteCore.createSetting(
                        Utilities.HTTP_OUTGOING_HEADER_COUNT_SENT,
                        headerCount.toString()
                    )
                }

                httpURLConnection!!.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                httpURLConnection!!.setRequestProperty("Content-Language", "en-US")
                httpURLConnection!!.requestMethod = requestMethod

                val queryStringSizeInBytes = queryString.toByteArray().size
                val contentLength =
                    queryStringSizeInBytes.toString() + (if (queryStringSizeInBytes > 1) " bytes" else "byte")
                sqliteCore.createSetting(
                    Utilities.HTTP_OUTGOING_CONTENT_LENGTH,
                    contentLength
                )

                if (requestMethod.equals("POST", ignoreCase = true)) {
                    httpURLConnection!!.setRequestProperty("Content-Length", contentLength)
                    sqliteCore.createSetting(
                        Utilities.HTTP_OUTGOING_DATA_OPEN_STREAM,
                        utilities?.currentTimeStamp
                    )
                    val wr = DataOutputStream(httpURLConnection!!.outputStream)
                    wr.writeBytes(queryString)
                    sqliteCore.createSetting(
                        Utilities.HTTP_OUTGOING_DATA_STREAM_SENT,
                        utilities?.currentTimeStamp
                    )
                    wr.flush()
                    wr.close()
                    sqliteCore.createSetting(
                        Utilities.HTTP_OUTGOING_DATA_CLOSE_STREAM,
                        utilities?.currentTimeStamp
                    )
                }

                resultHTTPClient = "SUCCESS"
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error connecting to server (MalformedURLException)"
            } catch (e: ProtocolException) {
                e.printStackTrace()
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error connecting to server (ProtocolException)"
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error connecting to server (UnsupportedEncodingException)"
            } catch (e: IOException) {
                e.printStackTrace()
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error connecting to server (IOException)"
            } catch (e: JSONException) {
                e.printStackTrace()
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error parsing JSON content"
            }

            responseData = getResponseData()
            Log.d("HttpClient", "runHttpProcess -> httpResponse -> $httpResponse")
            Log.d("HttpClient", "runHttpProcess -> responseData -> $responseData")
            parseHTTPResponse()
            if (httpResponse != "") {
                callback(null, httpResponse)
            }
        }
    }

    open fun callback(responseData: JSONObject?, message: String?) {}

    fun runHttpClient() {
        try {
            executorService = Executors.newFixedThreadPool(1)
            val task = HttpTask()
            future = executorService.submit(task)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("HttpClient", "runHttpClient -> error -> ${e.message}")
            callback(null, e.message)
        }
    }

    fun parseHTTPResponse() {
        if (resultHTTPClient.equals("SUCCESS", ignoreCase = true)) {
            try {
                callback(responseData, null)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    private val variablesQuery: String
        get() {
            val variablesBuilder = StringBuilder()
            var streamContents = ""
            try {
                if (sqliteCore.getSetting(Utilities.LOCATION_SETTING)
                        .equals("YES", ignoreCase = true)
                ) {
                    try {
                        variableData
                            .put(
                                JSONObject()
                                    .put("name", Utilities.LATITUDE)
                                    .put(
                                        "value",
                                        sqliteCore.getSetting(Utilities.LATITUDE)
                                    )
                            )
                            .put(
                                JSONObject()
                                    .put("name", Utilities.LONGITUDE)
                                    .put(
                                        "value",
                                        sqliteCore.getSetting(Utilities.LONGITUDE)
                                    )
                            )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val variableCount = variableData.length()
                if (variableCount > 0) {
                    var stringName: String

                    var stringValue: String

                    for (i in 0 until variableCount) {
                        val record = variableData.getJSONObject(i)
                        stringName = record.getString("name").trim { it <= ' ' }
                        stringValue = record.getString("value").trim { it <= ' ' }
                        if (requestMethod.equals("GET", ignoreCase = true)) {
                            if (sqliteCore.getSetting(Utilities.ENCODE_GET_VALUES_SETTING)
                                    .equals("YES", ignoreCase = true)
                            ) {
                                stringValue = String(
                                    Base64.encode(
                                        stringValue.toByteArray(),
                                        Base64.DEFAULT
                                    )
                                )
                            }
                        } else {
                            if (sqliteCore.getSetting(Utilities.ENCODE_POST_VALUES_SETTING)
                                    .equals("YES", ignoreCase = true)
                            ) {
                                stringValue = String(
                                    Base64.encode(
                                        stringValue.toByteArray(),
                                        Base64.DEFAULT
                                    )
                                )
                            }
                        }

                        variablesBuilder.append(stringName).append("=").append(stringValue)
                            .append("&")
                    }
                    streamContents = utilities?.removeLastChar(variablesBuilder.toString()) ?: ""

                    if (requestMethod.equals("GET", ignoreCase = true)) {
                        if (sqliteCore.getSetting(Utilities.ENCODE_GET_QUERY_STRING)
                                .equals("YES", ignoreCase = true)
                        ) {
                            streamContents = String(
                                Base64.encode(
                                    streamContents.toByteArray(),
                                    Base64.DEFAULT
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            sqliteCore.createSetting(
                Utilities.HTTP_OUTGOING_VARIABLES_SENT,
                streamContents
            )
            return streamContents
        }

    private fun getResponseData(): JSONObject {
        val headers = JSONObject()
        val output = JSONObject()
        val messageBuilder = StringBuilder()
        sqliteCore.createSetting(
            Utilities.HTTP_INCOMING_BEGIN,
            utilities?.currentTimeStamp
        )

        try {
            sqliteCore.createSetting(
                Utilities.HTTP_INCOMING_CODE,
                httpURLConnection!!.responseCode.toString()
            )
            sqliteCore.createSetting(
                Utilities.HTTP_INCOMING_DESCRIPTION,
                utilities?.getHttpResponseTypeDescription(
                    httpURLConnection!!.responseCode
                )
            )

            if (httpURLConnection!!.responseCode == HttpURLConnection.HTTP_OK) {
                resultHTTPClient = "SUCCESS"
                sqliteCore.createSetting(
                    Utilities.HTTP_INCOMING_BEGIN_READING_CONTENTS,
                    utilities?.currentTimeStamp
                )
                val bufferedReader = BufferedReader(
                    InputStreamReader(
                        httpURLConnection!!.inputStream, StandardCharsets.UTF_8
                    )
                )
                var line: String?
                while (true) {
                    if ((bufferedReader.readLine().also { line = it }) == null) break
                    messageBuilder.append(line)
                }
                bufferedReader.close()
                httpResponse = messageBuilder.toString()
                sqliteCore.createSetting(
                    Utilities.HTTP_INCOMING_END_READING_CONTENTS,
                    utilities?.currentTimeStamp
                )

                val httpResponseSizeInBytes = httpResponse.toByteArray().size
                val contentLength =
                    httpResponseSizeInBytes.toString() + (if (httpResponseSizeInBytes > 1) " bytes" else "byte")
                sqliteCore.createSetting(
                    Utilities.HTTP_INCOMING_CONTENTS_LENGTH,
                    contentLength
                )
            } else {
                resultHTTPClient = "EXCEPTION"
                httpResponse = "Error connecting to server (Negative Response)"
            }
            output
                .put("response_code", httpURLConnection!!.responseCode)
                .put(
                    "response_code_description", utilities?.getHttpResponseTypeDescription(
                        httpURLConnection!!.responseCode
                    ) ?: ""
                )
                .put("response_code_message", httpURLConnection!!.responseMessage)
                .put("response_message", httpResponse)

            val map = httpURLConnection!!.headerFields
            var headerCount = 0
            var headerValues: StringBuilder
            for (entry in map.entries) {
                headerCount++
                var headerKey = entry.key
                headerKey = headerKey ?: "."

                val headerValuesList = entry.value
                val iterator = headerValuesList.iterator()
                headerValues = StringBuilder()
                if (iterator.hasNext()) {
                    headerValues.append(iterator.next())

                    while (iterator.hasNext()) {
                        headerValues
                            .append(", ")
                            .append(iterator.next())
                    }
                }

                headers.put(headerKey, headerValues.toString())
            }
            sqliteCore.createSetting(
                Utilities.HTTP_INCOMING_HEADER_COUNT,
                headerCount.toString()
            )
            output.put("headers", headers)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return output
    }

    fun cancel() {
        future!!.cancel(true)
        executorService.shutdown()
    }
}