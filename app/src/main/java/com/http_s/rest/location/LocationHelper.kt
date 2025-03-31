package com.http_s.rest.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.http_s.rest.MainActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val TAG = "LocationHelper"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    /**
     * Requests the current GPS location with proper permission handling.
     *
     * @return A [Location] object if successful, null otherwise.
     */
    @SuppressLint("MissingPermission")
    suspend fun getGpsLocation(): Location? = suspendCancellableCoroutine { continuation ->
        if (!hasLocationPermission()) {
            Log.e(TAG, "Location permission not granted.")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        if (!isLocationEnabled()) {
            Log.e(TAG, "Location is not enabled.")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    Log.d(TAG, "Location received: ${location.latitude}, ${location.longitude}")
                    continuation.resume(location)
                } else {
                    Log.e(TAG, "Location is null.")
                    continuation.resume(null)
                }
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        continuation.invokeOnCancellation {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Checks if the app has the necessary location permissions.
     *
     * @return True if the permissions are granted, false otherwise.
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if location services are enabled on the device.
     *
     * @return True if location is enabled, false otherwise.
     */
    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Requests location permissions from the user.
     *
     * @param activity The activity requesting the permissions.
     */
    fun requestLocationPermissions(activity: MainActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}