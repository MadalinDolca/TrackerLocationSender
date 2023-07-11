package com.madalin.trackerlocationsender.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.madalin.trackerlocationsender.models.Coordinates
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme
import com.madalin.trackerlocationsender.utils.CoordinateType

@Composable
fun CoordinatesItem(coordinates: Coordinates) {
    Column(
        modifier = Modifier
            .background(color = Color.Gray, shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CoordinateNameText("Latitude")
            CoordinateValueText(coordinates.latitude, CoordinateType.LATITUDE)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CoordinateNameText("Longitude")
            CoordinateValueText(coordinates.longitude, CoordinateType.LONGITUDE)
        }
    }
}

@Composable
fun CoordinateNameText(text: String) {
    Text(
        text = "$text:",
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
fun CoordinateValueText(value: Double, coordinateType: CoordinateType) {
    val notation = if (coordinateType == CoordinateType.LATITUDE) "° N" else "° W"

    Text(
        text = "$value$notation",
        color = Color.Blue
    )
}

@Preview
@Composable
fun CoordinatesItemPreview() {
    TrackerLocationSenderTheme() {
        CoordinatesItem(coordinates = Coordinates(1234.0, 5231.0))
    }
}