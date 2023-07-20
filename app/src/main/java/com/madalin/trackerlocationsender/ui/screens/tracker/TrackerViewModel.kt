package com.madalin.trackerlocationsender.ui.screens.tracker

import androidx.lifecycle.ViewModel
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.ClientCredentials
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.models.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrackerViewModel : ViewModel() {
    private val _coordinatesListState = MutableStateFlow<List<Coordinates>>(emptyList())
    val coordinatesListState: StateFlow<List<Coordinates>> = _coordinatesListState

    @Deprecated("No longer used in here. Switch to LocationWorker.")
    val mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port, ClientCredentials.clientId)

    fun updateCoordinatesList(newCoordinatesMessage: String) {
        val (latitudeString, longitudeString) = newCoordinatesMessage.split(",")
        val latitude = latitudeString.toDoubleOrNull() ?: 0.0
        val longitude = longitudeString.toDoubleOrNull() ?: 0.0
        val updatedList = _coordinatesListState.value + Coordinates(latitude, longitude)

        _coordinatesListState.value = updatedList
    }
}