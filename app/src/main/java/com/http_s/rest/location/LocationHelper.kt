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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

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

        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    Log.d(TAG, "Location received: ${location.latitude}, ${location.longitude}")
                    continuation.resume(location)
                } else {
                    Log.e(TAG, "Location is null.")
                    continuation.resume(null)
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        locationCallback = callback

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5_000)
            .setMaxUpdateDelayMillis(10_000)
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        continuation.invokeOnCancellation {
            locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
            locationCallback = null
        }
    }

    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    companion object {
        private const val TAG = "LocationHelper"
    }
}
