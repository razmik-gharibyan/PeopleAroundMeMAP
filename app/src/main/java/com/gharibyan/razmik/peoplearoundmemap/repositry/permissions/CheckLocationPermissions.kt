package com.gharibyan.razmik.peoplearoundmemap.repositry.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

class CheckLocationPermissions(val context: Context): CheckLocationPermissionsInter {

    val FINE_LOCATION: String = android.Manifest.permission.ACCESS_FINE_LOCATION
    val COARSE_LOCATION: String = android.Manifest.permission.ACCESS_COARSE_LOCATION

    override suspend fun checkLocationPermissions(): Boolean {
        if(ContextCompat.checkSelfPermission(context,FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            && ContextCompat.checkSelfPermission(context,COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

}