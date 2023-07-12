package com.madalin.trackerlocationsender.ui.screens.tracker

import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.models.Coordinates
import com.madalin.trackerlocationsender.utils.LocationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class TrackerViewModel : ViewModel() {
    private val _coordinatesListState = MutableStateFlow<List<Coordinates>>(emptyList())
    val coordinatesListState: StateFlow<List<Coordinates>> = _coordinatesListState
    val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port)

    fun updateCoordinatesList(newCoordinatesMessage: String) {
        val (latitudeString, longitudeString) = newCoordinatesMessage.split(",")
        val latitude = latitudeString.toDoubleOrNull() ?: 0.0
        val longitude = longitudeString.toDoubleOrNull() ?: 0.0
        val updatedList = _coordinatesListState.value + Coordinates(latitude, longitude)

        _coordinatesListState.value = updatedList
    }
}