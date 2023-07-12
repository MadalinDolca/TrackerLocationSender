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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var outputData: Data

    override fun doWork(): Result {
        // connects this MQTT client to the broker with the given credentials
        val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port)
        mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val coordinatesMessage = "${location.latitude},${location.longitude}"

                        if (mqttClient.isConnected()) {
                            mqttClient.publishToTopic(Topic.tracker_location, coordinatesMessage)
                            outputData = Data.Builder().putString(DataKeys.LOCATION_COORDINATES, coordinatesMessage).build()
                            Log.d("LocationWorker", "Location coordinates sent: $coordinatesMessage")
                        }
                    }
                }
            Log.d("LocationWorker", "Location updates started")
        } else {
            Log.e("LocationWorker", "Location access hasn't been started")
        }

        return Result.success() //return Result.success(outputData)
    }

    override fun onStopped() {
        super.onStopped()
        Log.d("LocationWorker", "ListenableWorker has stopped")
    }
}