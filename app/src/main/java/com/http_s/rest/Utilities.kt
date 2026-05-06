package com.http_s.rest

import android.content.Context
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utilities internal constructor(private val context: Context) {
    fun getFileContentFromAssets(name: String): String? {
        return try {
            context.assets.open(name).use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }
        } catch (e: IOException) {
            null
        }
    }

    fun getHttpResponseTypeDescription(code: Int): String {
        return when (code) {
            200 -> "HTTP OK"
            201 -> "HTTP CREATED"
            202 -> "HTTP ACCEPTED"
            203 -> "HTTP NOT_AUTHORITATIVE"
            204 -> "HTTP NO_CONTENT"
            205 -> "HTTP RESET"
            206 -> "HTTP PARTIAL"
            300 -> "HTTP MULT_CHOICE"
            301 -> "HTTP MOVED_PERM"
            302 -> "HTTP MOVED_TEMP"
            303 -> "HTTP SEE_OTHER"
            304 -> "HTTP NOT_MODIFIED"
            305 -> "HTTP USE_PROXY"
            400 -> "HTTP BAD_REQUEST"
            401 -> "HTTP UNAUTHORIZED"
            402 -> "HTTP PAYMENT_REQUIRED"
            403 -> "HTTP FORBIDDEN"
            404 -> "HTTP NOT_FOUND"
            405 -> "HTTP BAD_METHOD"
            406 -> "HTTP NOT_ACCEPTABLE"
            407 -> "HTTP PROXY_AUTH"
            408 -> "HTTP CLIENT_TIMEOUT"
            409 -> "HTTP CONFLICT"
            410 -> "HTTP GONE"
            411 -> "HTTP LENGTH_REQUIRED"
            412 -> "HTTP PRECON_FAILED"
            413 -> "HTTP ENTITY_TOO_LARGE"
            414 -> "HTTP REQ_TOO_LONG"
            415 -> "HTTP UNSUPPORTED_TYPE"
            500 -> "HTTP INTERNAL_ERROR"
            501 -> "HTTP NOT_IMPLEMENTED"
            502 -> "HTTP BAD_GATEWAY"
            503 -> "HTTP UNAVAILABLE"
            504 -> "HTTP GATEWAY_TIMEOUT"
            505 -> "HTTP VERSION"
            else -> "HTTP $code"
        }
    }

    val currentTimeStamp: String
        get() = SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a", Locale.US).format(Date())

    companion object {
        const val LATITUDE: String = "LATITUDE"
        const val LONGITUDE: String = "LONGITUDE"
        const val LOCATION_SETTING: String = "ADD_LOCATION"
        const val ENCODE_GET_QUERY_STRING: String = "ENCODE_GET_QUERY_STRING"
        const val ENCODE_GET_VALUES_SETTING: String = "ENCODE_GET_VALUES"
        const val ENCODE_POST_VALUES_SETTING: String = "ENCODE_POST_VALUES"
        const val HTTP_URL: String = "HTTP_URL"
        const val HTTP_CONNECTION_OPENED: String = "HTTP_CONNECTION_OPENED"
        const val HTTP_OUTGOING_HEADER_SENT: String = "HTTP_OUTGOING_HEADER_SENT"
        const val HTTP_OUTGOING_HEADER_COUNT_SENT: String = "HTTP_OUTGOING_HEADER_COUNT_SENT"
        const val HTTP_OUTGOING_CONTENT_LENGTH: String = "HTTP_OUTGOING_CONTENT_LENGTH"
        const val HTTP_OUTGOING_DATA_OPEN_STREAM: String = "HTTP_OUTGOING_DATA_OPEN_STREAM"
        const val HTTP_OUTGOING_DATA_STREAM_SENT: String = "HTTP_OUTGOING_DATA_STREAM_SENT"
        const val HTTP_OUTGOING_DATA_CLOSE_STREAM: String = "HTTP_OUTGOING_DATA_CLOSE_STREAM"
        const val HTTP_OUTGOING_VARIABLES_SENT: String = "HTTP_OUTGOING_VARIABLES_SENT"
        const val HTTP_INCOMING_BEGIN: String = "HTTP_INCOMING_BEGIN"
        const val HTTP_INCOMING_CODE: String = "HTTP_INCOMING_CODE"
        const val HTTP_INCOMING_DESCRIPTION: String = "HTTP_INCOMING_DESCRIPTION"
        const val HTTP_INCOMING_BEGIN_READING_CONTENTS: String = "HTTP_INCOMING_BEGIN_READING_CONTENTS"
        const val HTTP_INCOMING_END_READING_CONTENTS: String = "HTTP_INCOMING_END_READING_CONTENTS"
        const val HTTP_INCOMING_CONTENTS_LENGTH: String = "HTTP_INCOMING_CONTENTS_LENGTH"
        const val HTTP_INCOMING_HEADER_COUNT: String = "HTTP_INCOMING_HEADER_COUNT"
    }
}
