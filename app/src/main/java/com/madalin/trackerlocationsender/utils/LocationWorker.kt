package com.madalin.trackerlocationsender.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.ClientCredentials
import com.madalin.trackerlocationsender.hivemq.Topic
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient

class LocationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port, ClientCredentials.clientId)

    private lateinit var outputData: Data
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    override fun doWork(): Result {
        // connects this MQTT client to the broker with the given credentials
        mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        // behavior to handle the received location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) { // most recent location information
                locationResult.lastLocation?.let { location ->
                    val coordinatesMessage = "${location.latitude},${location.longitude}"

                    if (mqttClient.isConnected()) {
                        mqttClient.publishToTopic(Topic.tracker_location, ClientCredentials.clientId, location.latitude, location.longitude)

                        outputData = Data.Builder().putString(DataKeys.LOCATION_COORDINATES, coordinatesMessage).build()
                    }

                    stopLocationUpdates()
                    locationCallback = null
                }
            }
        }

        startLocationUpdates()

        return Result.success() // Result.success(outputData)
    }

    override fun onStopped() {
        super.onStopped()
        Log.d("LocationWorker", "ListenableWorker has stopped")
    }

    /**
     * Starts requesting location updates based on [LocationRequest] preferences having
     * [LOCATION_UPDATE_INTERVAL] as interval if the location permissions are granted.
     */
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            locationCallback?.let { fusedLocationClient.requestLocationUpdates(locationRequest, it, Looper.getMainLooper()) }
        } else {
            Log.e("LocationWorker", "Location permission hasn't been granted")
        }
    }

    /**
     * Removes all location updates of [fusedLocationClient] that have [locationCallback] as a callback.
     */
    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}