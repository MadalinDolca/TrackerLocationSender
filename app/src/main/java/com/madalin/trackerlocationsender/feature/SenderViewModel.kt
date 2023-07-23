package com.madalin.trackerlocationsender.feature

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.ClientCredentials
import com.madalin.trackerlocationsender.hivemq.Topic
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.util.DataKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import java.util.concurrent.TimeUnit

class SenderViewModel : ViewModel() {
    private val uiStateInternal = MutableStateFlow(SenderUiState())
    val uiState = uiStateInternal.asStateFlow()

    private val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port, ClientCredentials.clientId)
    private var workManager: WorkManager? = null
    private var enqueuedworkId: UUID? = null

    lateinit var locationProvider: LocationProvider

    /**
     * Updates [SenderUiState.senderId] with the given [newId].
     * If [SenderUiState.isWorkerPublishing] is `true` it recalls [startPublishingWithWorkManager].
     * @param newId new sender ID
     * @param context context to pass for the worker
     */
    fun updateSenderId(newId: String, context: Context) {
        uiStateInternal.update { it.copy(senderId = newId) }

        if (uiStateInternal.value.isWorkerPublishing) {
            startPublishingWithWorkManager(context)
        }
    }

    /**
     * Connects and starts publishing location updates to the MQTT broker with [SenderUiState.senderId].
     * Location is received with [LocationProvider.setOnLocationReceivedCallback] and
     * started with [LocationProvider.startLocationUpdates].
     * Sets [SenderUiState.isPublishing] to `true`.
     * Updates the coordinates list with [updateCoordinatesList].
     * @param context [Context] to use in creating a [LocationProvider]
     */
    fun startPublishing(context: Context) {
        uiStateInternal.update { it.copy(isPublishing = true) }
        mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)
        locationProvider = LocationProvider(context)

        locationProvider.setOnLocationReceivedCallback { location ->
            if (mqttClient.isConnected()) {
                mqttClient.publishToTopic(
                    Topic.tracker_location,
                    uiStateInternal.value.senderId,
                    location.latitude,
                    location.longitude
                )

                updateCoordinatesList(location)
            }
        }

        locationProvider.startLocationUpdates()
    }

    /**
     * Stops publishing location updates and disconnects the MQTT client.
     * Sets [SenderUiState.isPublishing] to `false`.
     */
    fun stopPublishing() {
        uiStateInternal.update { it.copy(isPublishing = false) }
        locationProvider.stopLocationUpdates()
        mqttClient.disconnect()
    }

    /**
     * Starts publishing location messages with the [WorkManager] by calling [createAndLaunchLocationWorkManager].
     * Sets [SenderUiState.isWorkerPublishing] to `true`.
     */
    fun startPublishingWithWorkManager(context: Context) {
        uiStateInternal.update { it.copy(isWorkerPublishing = true) }
        createAndLaunchLocationWorkManager(context)
    }

    /**
     * Stops publishing location messages with the [WorkManager] by cancelling the execution of the
     * periodic work. Sets [SenderUiState.isWorkerPublishing] to `false`.
     */
    fun stopPublishingWithWorkManager() {
        uiStateInternal.update { it.copy(isWorkerPublishing = false) }
        enqueuedworkId?.let { workManager?.cancelWorkById(it) }
    }

    /**
     * Updates [SenderUiState.coordinatesList] with the given [newCoordinates].
     * @param newCoordinates the new coordinates to add
     */
    fun updateCoordinatesList(newCoordinates: Location) {
        uiStateInternal.update { currentState ->
            val updatedCoordinatesList = currentState.coordinatesList.toMutableList()
            updatedCoordinatesList.add(newCoordinates)
            currentState.copy(coordinatesList = updatedCoordinatesList)
        }
    }

    /**
     * Creates a constrained [PeriodicWorkRequest] with the [SenderUiState.senderId] as an input
     * data that gets enqueued in order to get and publish the current location of the device.
     */
    private fun createAndLaunchLocationWorkManager(context: Context) {
        // creates the WorkManager instance
        workManager = WorkManager.getInstance(context)

        // requirements that need to be met before a WorkRequest can run
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // acceptable battery level
            .setRequiredNetworkType(NetworkType.CONNECTED) // working network connection
            .build()

        // creates an InputData with the updated uiState
        val inputData = Data.Builder()
            .putString(DataKeys.SENDER_ID, uiStateInternal.value.senderId)
            .build()

        // creates the periodic work request with the constraints
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        // obtains the work request ID
        enqueuedworkId = workRequest.id

        // enqueues the work request
        workManager?.enqueue(workRequest)

        // observes the work request status
        /*workManager?.getWorkInfoByIdLiveData(workRequest.id)
            ?.observe(context) { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MainActivity", "Worker succeeded")

                    // obtains the new coordinates from the WorkRequest and updates the list
                    val newCoordinates = workInfo.outputData.getString(DataKeys.LOCATION_COORDINATES)

                    if (newCoordinates != null) {
                        updateCoordinatesList(newCoordinates)
                        Log.d("MainActivity", "Received coordinates $newCoordinates")
                    }
                }
            }*/
    }
}