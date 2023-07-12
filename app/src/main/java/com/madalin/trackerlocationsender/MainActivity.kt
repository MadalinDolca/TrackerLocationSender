package com.madalin.trackerlocationsender

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.madalin.trackerlocationsender.ui.screens.tracker.TrackerScreen
import com.madalin.trackerlocationsender.ui.screens.tracker.TrackerViewModel
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme
import com.madalin.trackerlocationsender.utils.DataKeys
import com.madalin.trackerlocationsender.utils.LocationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val trackerViewModel by viewModels<TrackerViewModel>()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackerLocationSenderTheme {
                TrackerScreen(trackerViewModel)
            }
        }

        // if location permissions are granted, the location WorkManager will launch
        if (checkAndRequestLocationPermissions(LOCATION_PERMISSION_REQUEST_CODE)) {
            createAndLaunchLocationWorkManager()
        }
    }

    /**
     * Checks if the permissions to use the location have been granted.
     * Requests permission if they have not yet been granted.
     * @param requestCode location permission request code
     * @return `true` if every permission has been granted, `false` otherwise
     */
    private fun Context.checkAndRequestLocationPermissions(requestCode: Int): Boolean {
        // list of permissions to request
        val permissionsList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        // filters the permissions that are not granted
        val permissionsToRequest = permissionsList.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // requests the permissions that are not granted
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), requestCode)
            return false
        }

        return true
    }

    /**
     * Creates a constrained periodic WorkRequest that gets enqueued in order to get and publish
     * the current location of the device.
     */
    private fun createAndLaunchLocationWorkManager() {
        // creates the WorkManager instance
        val workManager = WorkManager.getInstance(this)

        // requirements that need to be met before a WorkRequest can run
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // acceptable battery level
            .setRequiredNetworkType(NetworkType.CONNECTED) // working network connection
            .build()

        // creates the periodic work request with the constraints
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        // enqueues the work request
        workManager.enqueue(workRequest)

        // observes the work request status
        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("MainActivity", "Worker succeeded")

                    // obtains the new coordinates from the WorkRequest and updates the list
                    val newCoordinates = workInfo.outputData.getString(DataKeys.LOCATION_COORDINATES)

                    if (newCoordinates != null) {
                        trackerViewModel.updateCoordinatesList(newCoordinates)
                        Log.d("MainActivity", "Received coordinates $newCoordinates")
                    }
                }
            }
    }

    // stops the execution of the periodic work
    // workManager.cancelWorkById(periodicWorkRequest.id)
}