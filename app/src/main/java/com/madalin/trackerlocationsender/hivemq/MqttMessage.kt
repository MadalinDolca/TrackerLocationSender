package com.madalin.trackerlocationsender.hivemq

data class MqttMessage(
    var clientId: String,
    var latitude: Double,
    var longitude: Double
)