package com.madalin.trackerlocationsender.ui.screen

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madalin.trackerlocationsender.R
import com.madalin.trackerlocationsender.feature.SenderViewModel
import com.madalin.trackerlocationsender.ui.component.CoordinatesItem
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

@Composable
fun TrackerScreen(senderViewModel: SenderViewModel) {
    val viewState by senderViewModel.uiState.collectAsState()
    val context = LocalContext.current.applicationContext

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SenderIdSection(
            context = context,
            updateSenderIdOnClick = { newId -> senderViewModel.updateSenderId(newId, context) }
        )

        Text(
            text = stringResource(R.string.publish_data_when_the_app_is_in_foreground),
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
        )
        ButtonPublishData(
            isPublishing = viewState.isPublishing,
            onStartClick = { senderViewModel.startPublishing(context) },
            onStopClick = { senderViewModel.stopPublishing() }
        )

        Text(
            text = stringResource(R.string.publish_data_even_when_the_app_is_not_in_the_foreground),
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
        )
        ButtonPublishDataWithWorkManager(
            isWorkerPublishing = viewState.isWorkerPublishing,
            onStartClick = { senderViewModel.startPublishingWithWorkManager(context) },
            onStopClick = { senderViewModel.stopPublishingWithWorkManager() }
        )

        CoordinatesListSection(
            isPublishing = viewState.isPublishing,
            isWorkerPublishing = viewState.isWorkerPublishing,
            coordinatesList = viewState.coordinatesList
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SenderIdSection(
    context: Context,
    updateSenderIdOnClick: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 17.sp),
            placeholder = { Text(text = stringResource(R.string.enter_sender_id_here)) }
        )

        Button(
            onClick = {
                updateSenderIdOnClick(textFieldValue.text)
                Toast.makeText(context, context.getString(R.string.sender_id_has_been_updated), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = stringResource(id = R.string.set_id),
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ButtonPublishData(
    isPublishing: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Button(
        onClick = {
            if (isPublishing) onStopClick()
            else onStartClick()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            text = if (isPublishing) stringResource(R.string.stop_publishing_data)
            else stringResource(R.string.start_publishing_data),
            fontSize = 20.sp
        )
    }
}

@Composable
fun ButtonPublishDataWithWorkManager(
    isWorkerPublishing: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Button(
        onClick = {
            if (isWorkerPublishing) onStopClick()
            else onStartClick()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            text = if (isWorkerPublishing) stringResource(R.string.stop_publishing_with_workmanager)
            else stringResource(R.string.start_publishing_with_workmanager),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CoordinatesListSection(
    isPublishing: Boolean,
    isWorkerPublishing: Boolean,
    coordinatesList: List<Location>
) {
    val lazyListState = rememberLazyListState()

    // shows the coordinates list if tracking is enabled
    if (isPublishing || isWorkerPublishing) {
        LaunchedEffect(coordinatesList) {
            // scrolls to the last item whenever the list updates
            if (coordinatesList.isNotEmpty()) {
                lazyListState.scrollToItem(coordinatesList.lastIndex)
                lazyListState.animateScrollToItem(coordinatesList.lastIndex)
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 10.dp),
            state = lazyListState,
            contentPadding = PaddingValues(top = 5.dp), // padding between items
            verticalArrangement = Arrangement.spacedBy(5.dp) //, reverseLayout = true
        ) {
            items(items = coordinatesList) { coordinates ->
                CoordinatesItem(coordinates = coordinates)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackerScreenPreview() {
    val senderViewModel = SenderViewModel()

    TrackerLocationSenderTheme {
        TrackerScreen(senderViewModel)
    }
}