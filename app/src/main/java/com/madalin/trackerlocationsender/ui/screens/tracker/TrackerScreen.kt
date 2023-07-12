package com.madalin.trackerlocationsender.ui.screens.tracker

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.madalin.trackerlocationsender.R
import com.madalin.trackerlocationsender.hivemq.ClientCredentials
import com.madalin.trackerlocationsender.ui.CoordinatesItem
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

@Composable
fun TrackerScreen(trackerViewModel: TrackerViewModel) {
    Log.d("TrackerScreen", "root")
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d("TrackerScreen", "Surface > Column")

            /*ButtonPublishData(
                isPublishing = isPublishingState.value,
                onStartClick = {
                    trackerViewModel.mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)
                    isPublishingState.value = true
                },
                onStopClick = {
                    trackerViewModel.mqttClient.disconnect()
                    isPublishingState.value = false
                }
            )*/

            ButtonPublishDataTest(trackerViewModel)
            CoordinatesListSection(trackerViewModel)
        }
    }
}

@Composable
fun ButtonPublishDataTest(trackerViewModel: TrackerViewModel) {
    Log.d("TrackerScreen", "ButtonPublishDataTest")
    val isPublishingState = remember { mutableStateOf(true) } // publishing ON by default

    Button(onClick = {
        if (isPublishingState.value) {
            trackerViewModel.mqttClient.disconnect()
            isPublishingState.value = false
        } else {
            trackerViewModel.mqttClient.connectToBroker(ClientCredentials.username, ClientCredentials.password)
            isPublishingState.value = true
        }
    }) {
        Text(
            text = if (isPublishingState.value) stringResource(R.string.stop_publishing_data)
            else stringResource(R.string.start_publishing_data)
        )
    }
}

// unused :(
@Composable
fun ButtonPublishData(
    isPublishing: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Log.d("TrackerScreen", "ButtonPublishData")
    Button(onClick = if (isPublishing) onStopClick else onStartClick) {
        Text(
            text = if (isPublishing) stringResource(R.string.stop_publishing_data)
            else stringResource(R.string.start_publishing_data)
        )
    }
}

@Composable
fun CoordinatesListSection(trackerViewModel: TrackerViewModel) {
    Log.d("TrackerScreen", "CoordinatesList")
    val coordinatesListState by trackerViewModel.coordinatesListState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(coordinatesListState) {
        Log.d("TrackerScreen", "CoordinatesList > LaunchedEffect")

        // scrolls to the last item whenever the list updates
        if (coordinatesListState.isNotEmpty()) {
            lazyListState.scrollToItem(coordinatesListState.lastIndex)
            lazyListState.animateScrollToItem(coordinatesListState.lastIndex)
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(all = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp) //, reverseLayout = true
    ) {
        items(items = coordinatesListState) { coordinates ->
            CoordinatesItem(coordinates = coordinates)
        }
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