package com.madalin.trackerlocationsender.feature

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.ClientCredentials
import com.madalin.trackerlocationsender.hivemq.Topic
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.util.DataKeys

class LocationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port, ClientCredentials.clientId)
    private val locationProvider = LocationProvider(context)
    private lateinit var outputData: Data

    override fun doWork(): Result {
        // Retrieve state values from input data
        val senderId = inputData.getString(DataKeys.SENDER_ID)

        // connects this MQTT client to the broker with the given credentials
        mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)

        // publish the received location updates only once
        locationProvider.setOnLocationReceivedCallback { location ->
            val coordinatesString = "${location.latitude}, ${location.longitude}"

            if (mqttClient.isConnected() && senderId != null) {
                mqttClient.publishToTopic(Topic.tracker_location, senderId, location.latitude, location.longitude)
                                outputData = Data.Builder().putString(DataKeys.LOCATION_COORDINATES, coordinatesString).build()
            }

            locationProvider.stopLocationUpdates()
        }

        locationProvider.startLocationUpdates()

        return Result.success() // Result.success(outputData)
    }

    override fun onStopped() {
        super.onStopped()
        locationProvider.stopLocationUpdates()
        Log.d("LocationWorker", "ListenableWorker has stopped")
    }
}