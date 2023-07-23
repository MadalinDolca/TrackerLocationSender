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
import com.madalin.trackerlocationsender.feature.SenderViewModel
import com.madalin.trackerlocationsender.ui.screen.TrackerScreen
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

class MainActivity : ComponentActivity() {
    private val senderViewModel by viewModels<SenderViewModel>()
    private val LOCATION_PERMISSION_REQUEST_CODE = 10203

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackerLocationSenderTheme {
                TrackerScreen(senderViewModel)
            }
        }

        // request location permission
        checkAndRequestLocationPermissions(LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted
                    // ...
                } else {
                    // permission not granted; request again
                    //checkAndRequestLocationPermissions(LOCATION_PERMISSION_REQUEST_CODE)
                    Log.e("MainActivity", "Location permission denied")
                }
            }
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
}