package com.studio29.gharibyan.peoplearoundmemap.ui.connection

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.studio29.gharibyan.peoplearoundmemap.repositry.connection.CheckConnections
import com.studio29.gharibyan.peoplearoundmemap.repositry.permissions.CheckLocationPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectionViewModel(private var context: Context): ViewModel() {

    //Initialisation
    private val checkConnections = CheckConnections(context)
    private val checkLocationPermissions = CheckLocationPermissions(context)

    // Live Data
    private var _networkAccessLD = MutableLiveData<Boolean>()
    private var _gpsAccessLD = MutableLiveData<Boolean>()
    private var _permissionLD = MutableLiveData<Boolean>()

    var networkAccess: LiveData<Boolean> = _networkAccessLD
    var gpsAccess: LiveData<Boolean> = _gpsAccessLD
    var permissionAccess: LiveData<Boolean> = _permissionLD

    // Vars
    var currentUserID: String? = null
    var registerNewUser: Boolean? = false
    var currentUserEmail: String? = null

    fun checkNetworkStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            _networkAccessLD.value = checkConnections.checkInternetAccess() && checkConnections.checkGooglePlayServicesEnabled()
        }
    }

    fun checkGPSStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            _gpsAccessLD.value = checkConnections.checkGPSEnabled()
        }
    }

    fun checkPermissionStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            _permissionLD.value = checkLocationPermissions.checkLocationPermissions()
        }
    }

    fun clearLiveData() {
        _networkAccessLD.value = null
    }

}