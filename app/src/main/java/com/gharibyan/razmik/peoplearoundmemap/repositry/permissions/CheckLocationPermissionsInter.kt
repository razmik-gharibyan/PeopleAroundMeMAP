package com.gharibyan.razmik.peoplearoundmemap.repositry.permissions

interface CheckLocationPermissionsInter {

    suspend fun checkLocationPermissions(): Boolean

}