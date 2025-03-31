package com.http_s.rest

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class Utilities internal constructor(private val context: Context) {

    fun requestGpsPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                1
            )
            return false
        }
    }

    fun getFileContentFromAssets(name: String): String? {
        try {
            val inputStream = context.assets.open(name)
            val streamSize = inputStream.available()
            val buffer = ByteArray(streamSize)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun removeLastChar(inputValue: String?): String {
        return if (inputValue.isNullOrEmpty())
            ""
        else
            (inputValue.substring(0, inputValue.length - 1))
    }

    fun getHttpResponseTypeDescription(code: Int): String {
        var output = ""
        when (code) {
            202 -> output = "HTTP ACCEPTED"
            502 -> output = "HTTP BAD_GATEWAY"
            405 -> output = "HTTP BAD_METHOD"
            400 -> output = "HTTP BAD_REQUEST"
            408 -> output = "HTTP CLIENT_TIMEOUT"
            409 -> output = "HTTP CONFLICT"
            201 -> output = "HTTP CREATED"
            413 -> output = "HTTP ENTITY_TOO_LARGE"
            403 -> output = "HTTP FORBIDDEN"
            504 -> output = "HTTP GATEWAY_TIMEOUT"
            410 -> output = "HTTP GONE"
            500 -> output = "HTTP INTERNAL_ERROR"
            411 -> output = "HTTP LENGTH_REQUIRED"
            301 -> output = "HTTP MOVED_PERM"
            302 -> output = "HTTP MOVED_TEMP"
            300 -> output = "HTTP MULT_CHOICE"
            406 -> output = "HTTP NOT_ACCEPTABLE"
            203 -> output = "HTTP NOT_AUTHORITATIVE"
            404 -> output = "HTTP NOT_FOUND"
            501 -> output = "HTTP NOT_IMPLEMENTED"
            304 -> output = "HTTP NOT_MODIFIED"
            204 -> output = "HTTP NO_CONTENT"
            200 -> output = "HTTP OK"
            206 -> output = "HTTP PARTIAL"
            402 -> output = "HTTP PAYMENT_REQUIRED"
            412 -> output = "HTTP PRECON_FAILED"
            407 -> output = "HTTP PROXY_AUTH"
            414 -> output = "HTTP REQ_TOO_LONG"
            205 -> output = "HTTP RESET"
            303 -> output = "HTTP SEE_OTHER"
            401 -> output = "HTTP UNAUTHORIZED"
            503 -> output = "HTTP UNAVAILABLE"
            415 -> output = "HTTP UNSUPPORTED_TYPE"
            305 -> output = "HTTP USE_PROXY"
            505 -> output = "HTTP VERSION"
        }

        return output
    }

    val getGpsLocation: Location?
        get() {
            try {
                var locationRetrieved = false
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                //Getting the location through the NETWORK is much faster and consumes only a fraction of the power and system resources necessary for the task
                //We are favoring this over GPS. Of course, if it doesn't give us the location, then the GPS code below will be executed
                val isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                            context,
                            permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return TODO;
                    }
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        (1000 * 10).toLong(),
                        10f, object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                            }

                            override fun onProviderEnabled(s: String) {
                            }

                            override fun onProviderDisabled(s: String) {
                            }
                        })

                    var location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    if (location != null) {
                        //If it could not get the current location through NETWORK, then it tries to get through GPS
                        //GPS consumes a large amount of battery and it's quite slow, so we are trying to avoid slow speeds and costly location queries

                        // getting GPS status

                        val isGPSEnabled =
                            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        // if GPS Enabled get lat/long using GPS Services
                        if (isGPSEnabled) {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(
                                    context,
                                    permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                //return TODO;
                            }
                            locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                (1000 * 10).toLong(),
                                10f, context as LocationListener
                            )

                            location =
                                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        }
                    }

                    return location
                }

                val a = context as Activity
                locationManager.removeUpdates(a as LocationListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    val currentTimeStamp: String?
        get() {
            try {
                val dateFormat =
                    SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a")
                return dateFormat.format(Date())
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

    companion object {
        private const val REQUEST_READ_CONTACTS = 444
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
        const val HTTP_INCOMING_BEGIN_READING_CONTENTS: String =
            "HTTP_INCOMING_BEGIN_READING_CONTENTS"
        const val HTTP_INCOMING_END_READING_CONTENTS: String = "HTTP_INCOMING_END_READING_CONTENTS"
        const val HTTP_INCOMING_CONTENTS_LENGTH: String = "HTTP_INCOMING_CONTENTS_LENGTH"
        const val HTTP_INCOMING_HEADER_COUNT: String = "HTTP_INCOMING_HEADER_COUNT"
    }
}
