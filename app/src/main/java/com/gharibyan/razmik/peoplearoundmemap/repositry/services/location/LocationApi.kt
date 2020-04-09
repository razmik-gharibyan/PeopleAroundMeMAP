package com.gharibyan.razmik.peoplearoundmemap.repositry.services.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*

class LocationApi(val context: Context) {

    // Constants
    private val UPDATE_INTERVAL: Long = 3000
    private val FASTERS_INTERVAL: Long = 2000

    // Initialization
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LiveData
    private var _LocationLD = MutableLiveData<Location>()
    var locationUpdate: LiveData<Location> = _LocationLD

    fun startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // Start location updates
        getLocation()

    }

    private fun getLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTERS_INTERVAL

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?: return
                val location = locationResult.lastLocation
                _LocationLD.value = location
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
    }
}