package com.gharibyan.razmik.peoplearoundmemap.repositry.connection

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class CheckConnections(private var context: Context): CheckConnectionsInter {

    val ERROR_DIALOG_REQUEST: Int = 2900

    override suspend fun checkInternetAccess(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    override suspend fun checkGooglePlayServicesEnabled(): Boolean {
        val googlePlayServicesStatus: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)

        if(googlePlayServicesStatus == ConnectionResult.SUCCESS) {
            return true
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(googlePlayServicesStatus)){
            val dialog: Dialog = GoogleApiAvailability.getInstance().getErrorDialog(context as Activity,googlePlayServicesStatus,ERROR_DIALOG_REQUEST)
            dialog.show()
        }
        return false

    }

    override suspend fun checkGPSEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}