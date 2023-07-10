package com.madalin.trackerlocationsender.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

@Composable
fun TrackerScreen() {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Greeting("Android")
        /*ButtonPublishData {
            mqttClient.publishMessage(
                Topic.tracker_location,
                "11243,432144"
            )*/
    }
}

@Composable
fun ButtonPublishData(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(text = "Publish data")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackerLocationSenderTheme {
        Greeting("Android")
    }
}