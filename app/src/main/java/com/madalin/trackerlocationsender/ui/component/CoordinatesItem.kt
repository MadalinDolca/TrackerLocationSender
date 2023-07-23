package com.madalin.trackerlocationsender.ui.component

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.madalin.trackerlocationsender.R
import com.madalin.trackerlocationsender.ui.theme.LightGray
import com.madalin.trackerlocationsender.util.CoordinateType

@Composable
fun CoordinatesItem(
    coordinates: Location
) {
    Column(
        modifier = Modifier
            .background(color = LightGray, shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CoordinateNameText(stringResource(R.string.latitude))
            CoordinateValueText(coordinates.latitude, CoordinateType.LATITUDE)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CoordinateNameText(stringResource(R.string.longitude))
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
        color = MaterialTheme.colorScheme.primary
    )
}