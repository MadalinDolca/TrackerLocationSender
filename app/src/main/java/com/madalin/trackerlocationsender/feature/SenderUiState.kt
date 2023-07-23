package com.madalin.trackerlocationsender.feature

import android.location.Location
import com.madalin.trackerlocationsender.hivemq.ClientCredentials

data class SenderUiState(
    val senderId: String = ClientCredentials.clientId,
    val isPublishing: Boolean = false,
    val isWorkerPublishing: Boolean = false,
    val coordinatesList: List<Location> = emptyList()
)