package com.madalin.trackerlocationsender.ui.screens.tracker

import androidx.lifecycle.ViewModel
import com.madalin.trackerlocationsender.hivemq.BrokerCredentials
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.models.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrackerViewModel : ViewModel() {
    private val _coordinatesListState = MutableStateFlow<List<Coordinates>>(emptyList())
    val coordinatesListState: StateFlow<List<Coordinates>> = _coordinatesListState

    var mqttClient = TrackerMqttClient(BrokerCredentials.host, BrokerCredentials.port)

    fun updateCoordinatesList(newCoordinates: Coordinates) {
        val updatedList = _coordinatesListState.value + newCoordinates
        _coordinatesListState.value = updatedList
    }
}