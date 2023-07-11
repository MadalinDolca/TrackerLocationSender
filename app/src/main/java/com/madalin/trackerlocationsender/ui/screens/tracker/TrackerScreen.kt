package com.madalin.trackerlocationsender.ui.screens.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.madalin.trackerlocationsender.ui.CoordinatesItem
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

@Composable
fun TrackerScreen(trackerViewModel: TrackerViewModel) {
    val coordinatesListState by trackerViewModel.coordinatesListState.collectAsState()
    val lazyListState = rememberLazyListState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column() {
            ButtonRequestLocationPermission(text = "Request permission") {}

            ButtonPublishData(text = "Publish data") {
                //mqttClient.publishMessage(Topic.tracker_location, "11243,432144")
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(all = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                reverseLayout = true
            ) {
                items(items = coordinatesListState) { coordinates ->
                    CoordinatesItem(coordinates = coordinates)
                }
            }

            LaunchedEffect(Unit) {
                lazyListState.scrollToItem(index = 0)
            }
        }
    }
}

@Composable
fun ButtonRequestLocationPermission(text: String, onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(text = text)
    }
}

@Composable
fun ButtonPublishData(text: String, onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun TrackerScreenPreview() {
    val trackerViewModel = TrackerViewModel()

    TrackerLocationSenderTheme {
        TrackerScreen(trackerViewModel)
    }
}