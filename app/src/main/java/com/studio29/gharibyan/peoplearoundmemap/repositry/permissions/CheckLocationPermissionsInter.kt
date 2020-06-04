package com.studio29.gharibyan.peoplearoundmemap.repositry.permissions

interface CheckLocationPermissionsInter {

    suspend fun checkLocationPermissions(): Boolean

}