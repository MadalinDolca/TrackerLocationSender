package com.madalin.trackerlocationsender.feature

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Provides location updates and methods to start and stop location fetching.
 * @param context context to use in order to create a [FusedLocationProviderClient]
 */
class LocationProvider(private val context: Context) {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    /**
     * Instantiates this [locationCallback] with a [LocationCallback] object and calls the given
     * [onReceived] when location result has been obtained.
     * @param onReceived behavior to handle the received location updates
     */
    fun setOnLocationReceivedCallback(onReceived: (Location) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) { // most recent location information
                locationResult.lastLocation?.let { location ->
                    onReceived(location)
                    Log.d("LocationProvider", "Current location: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    /**
     * Starts requesting location updates based on [LocationRequest] preferences every 5 seconds
     * if the location permissions are granted.
     * **Must be called after [setOnLocationReceivedCallback].**
     */
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationCallback?.let { fusedLocationClient.requestLocationUpdates(locationRequest, it, Looper.getMainLooper()) }
        } else {
            Log.e("LocationProvider", "Location permission hasn't been granted")
        }
    }

    /**
     * Removes all location updates of this [fusedLocationClient] that have [locationCallback] as a
     * callback.
     */
    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
        Log.d("LocationProvider", "Location updates stopped")
    }
}