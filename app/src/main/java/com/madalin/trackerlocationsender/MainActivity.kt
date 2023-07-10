package com.madalin.trackerlocationsender

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.madalin.trackerlocationsender.hivemq.TrackerMqttClient
import com.madalin.trackerlocationsender.ui.screens.TrackerScreen
import com.madalin.trackerlocationsender.ui.theme.TrackerLocationSenderTheme

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val LOCATION_UPDATE_INTERVAL = 5000L

    private var mqttClient = TrackerMqttClient()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TrackerLocationSenderTheme { TrackerScreen() } }

        checkLocationPermission(LOCATION_PERMISSION_REQUEST_CODE)

        //mqttClient.connectToBroker()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // behavior to handle the received location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) { // most recent location information
                locationResult.lastLocation?.let { location ->
                    Log.d("Coordinates", "Lat: ${location.latitude} Lon: ${location.longitude}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // if the permission has been granted, start location updates
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                // permission denied
                // ...
            }
        }
    }

    /**
     * Check if the permissions to use the location have been granted.
     * Requests permission if they have not yet been granted.
     * If granted, location updates will start via [startLocationUpdates].
     * @param requestCode location permission request code
     */
    private fun Context.checkLocationPermission(requestCode: Int) {
        if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) && // precise location
            checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) && // approximate location
            checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) // access location in the background
        ) {
            startLocationUpdates()
            return // exits the method
        }

        // if not granted, requests them
        val permissionsList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        requestPermissions(permissionsList, requestCode)
    }

    /**
     * Checks if the declared [permission] has been granted.
     * @param permission to check if granted
     * @return `true` if granted, `false` otherwise
     */
    private fun Context.checkSinglePermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts requesting location updates based on [LocationRequest] preferences having
     * [LOCATION_UPDATE_INTERVAL] as interval if the location permissions are granted.
     */
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL).build()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            Toast.makeText(this, getString(R.string.location_access_hasnt_been_granted), Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Removes all location updates of [fusedLocationClient] that have [locationCallback] as a callback.
     */
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /* mqttClient = Mqtt5Client.builder()
     .serverHost(BrokerCredentials.serverAddress)
     .identifier(ClientCredentials.clientId)
     .sslWithDefaultConfig()
     .automaticReconnectWithDefaultConfig()
     .simpleAuth()
     .username(ClientCredentials.username)
     .password(ClientCredentials.password.toByteArray())
     .applySimpleAuth()
     .buildAsync()*/

    //mqttClient.connect()

    /* mqttClient.connect()
         .whenComplete { connectResult, throwable ->
             if (throwable != null) {
                 // Handle connection failure
                 throwable.printStackTrace()
             } else {
                 if (connectResult != null) {
                     // Connection successful, you can now publish data
                     Log.d("CONNACK", "Connected")
                 }
             }
         }*/

    fun publishData() {
        /*mqttClient.publishWith()
            .topic(Topic.tracker_location)
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload("53425,12346".toByteArray())
            .send()
            .whenComplete { publish, throwable ->
                if (throwable != null) {
                    // Handle publish failure
                    throwable.printStackTrace()
                } else {
                    // Handle publish success
                    Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
                }
            }*/
    }
}